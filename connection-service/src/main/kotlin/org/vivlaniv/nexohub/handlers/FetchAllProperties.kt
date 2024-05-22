package org.vivlaniv.nexohub.handlers

import org.vivlaniv.nexohub.Application
import org.vivlaniv.nexohub.FetchDevicesPropertiesTask
import org.vivlaniv.nexohub.FetchDevicesPropertiesTaskResult
import org.vivlaniv.nexohub.GetDevicesPropertiesTask
import org.vivlaniv.nexohub.GetDevicesPropertiesTaskResult
import org.vivlaniv.nexohub.GetSavedDevicesTask
import org.vivlaniv.nexohub.GetSavedDevicesTaskResult
import org.vivlaniv.nexohub.SaveSensorsTask
import org.vivlaniv.nexohub.util.async.requireAsyncCompleter
import org.vivlaniv.nexohub.util.async.sendAsync
import org.vivlaniv.nexohub.util.mqtt.publish
import org.vivlaniv.nexohub.util.mqtt.subscribe
import org.vivlaniv.nexohub.util.redis.publish
import java.util.concurrent.TimeoutException

fun Application.configureFetchAllPropertiesHandler() {
    val getSavedDevicesTopic = props.getProperty("topic.saved.devices", "devices/saved/get")
    val getDevicesPropertiesTopic = props.getProperty("topic.get.devices.properties", "devices/properties/get")
    val saveSensorsTopic = props.getProperty("topic.save.sensors", "sensors/save")

    redis.requireAsyncCompleter<GetSavedDevicesTaskResult>("$getSavedDevicesTopic/out")

    redis.requireAsyncCompleter<GetDevicesPropertiesTaskResult>("$getDevicesPropertiesTopic/out")

    mqtt.subscribe<FetchDevicesPropertiesTask>("+/fetch/props/in") { topic, request ->
        val user = topic.substringBefore('/')

        val response = try {
            val savedDevicesIds = (sendAsync(redis,
                "$getSavedDevicesTopic/in",
                GetSavedDevicesTask(user = user)
            ).await() as GetSavedDevicesTaskResult)
                .devices.mapTo(mutableListOf()) { it.id }

            request.include?.let { savedDevicesIds.retainAll(it) }

            val getDevicesPropsResult = sendAsync(redis,
                "$getDevicesPropertiesTopic/in",
                GetDevicesPropertiesTask(user = user, include = savedDevicesIds)
            ).await() as GetDevicesPropertiesTaskResult

            if (getDevicesPropsResult.code != 0) {
                FetchDevicesPropertiesTaskResult(request.id, getDevicesPropsResult.code, getDevicesPropsResult.errorMessage)
            } else {
                redis.publish("$saveSensorsTopic/in",
                    SaveSensorsTask(sensors = getDevicesPropsResult.properties!!)
                )
                FetchDevicesPropertiesTaskResult(request.id, properties = getDevicesPropsResult.properties)
            }
        } catch (e: TimeoutException) {
            FetchDevicesPropertiesTaskResult(request.id, 1, "timeout occurred, try again later")
        }

        mqtt.publish("${user}/fetch/props/out", response)
    }

    log.info("Fetch all properties handler configured")
}