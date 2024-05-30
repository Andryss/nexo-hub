package org.vivlaniv.nexohub.connection.handlers

import org.vivlaniv.nexohub.common.SavedDevice
import org.vivlaniv.nexohub.common.task.*
import org.vivlaniv.nexohub.common.util.mqtt.publish
import org.vivlaniv.nexohub.common.util.mqtt.subscribe
import org.vivlaniv.nexohub.common.util.redis.publish
import org.vivlaniv.nexohub.connection.Application
import org.vivlaniv.nexohub.connection.util.requireAsyncCompleter
import org.vivlaniv.nexohub.connection.util.sendAsync
import java.util.concurrent.TimeoutException

fun Application.configureFetchSavedDevicesHandler() {
    val getSavedDevicesTopic = props.getProperty("topic.saved.devices", "devices/saved/get")
    val getDevicesTopic = props.getProperty("topic.get.devices", "devices/get")
    val saveSensorsTopic = props.getProperty("topic.save.sensors", "sensors/save")

    redis.requireAsyncCompleter<GetSavedDevicesTaskResult>("$getSavedDevicesTopic/out")
    redis.requireAsyncCompleter<GetDevicesTaskResult>("$getDevicesTopic/out")

    mqtt.subscribe<FetchSavedDevicesTask>("+/fetch/devices/in") { topic, request ->
        val user = topic.substringBefore('/')

        val response = try {
            val savedDevices = (sendAsync(redis,
                "$getSavedDevicesTopic/in",
                GetSavedDevicesTask(user = user)
            ).await() as GetSavedDevicesTaskResult)
                .devices

            val savedDevicesMap = mapOf(*savedDevices.map { it.id to it }.toTypedArray())

            val getDevicesResult = sendAsync(redis,
                "$getDevicesTopic/in",
                GetDevicesTask(user = user, include = savedDevicesMap.keys.toList())
            ).await() as GetDevicesTaskResult

            if (getDevicesResult.code != 0) {
                FetchSavedDevicesTaskResult(tid = request.id, code = getDevicesResult.code, errorMessage = getDevicesResult.errorMessage)
            } else {
                val devices = getDevicesResult.devices!!
                redis.publish("$saveSensorsTopic/in",
                    SaveSensorsTask(sensors = mapOf(*devices.map { it.id to it.properties }.toTypedArray()))
                )
                val savedDevicesResponse = devices.map { SavedDevice(it.id, it.type,
                    savedDevicesMap[it.id]?.room, savedDevicesMap[it.id]?.alias, it.properties) }
                FetchSavedDevicesTaskResult(tid = request.id, devices = savedDevicesResponse)
            }
        } catch (e: TimeoutException) {
            FetchSavedDevicesTaskResult(request.id, 1, "timeout occurred, try again later")
        }

        mqtt.publish("${user}/fetch/devices/out", response)
    }

    log.info("Fetch saved devices handler configured")
}