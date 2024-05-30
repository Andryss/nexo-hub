package org.vivlaniv.nexohub.connection.handlers

import org.vivlaniv.nexohub.common.task.*
import org.vivlaniv.nexohub.common.util.mqtt.publish
import org.vivlaniv.nexohub.common.util.mqtt.subscribe
import org.vivlaniv.nexohub.connection.Application
import org.vivlaniv.nexohub.connection.util.requireAsyncCompleter
import org.vivlaniv.nexohub.connection.util.sendAsync
import java.util.concurrent.TimeoutException

fun Application.configureSearchDeviceHandler() {
    val getSavedDevicesTopic = props.getProperty("topic.saved.devices", "devices/saved/get")
    val getDevicesTopic = props.getProperty("topic.get.devices", "devices/get")

    redis.requireAsyncCompleter<GetSavedDevicesTaskResult>("$getSavedDevicesTopic/out")
    redis.requireAsyncCompleter<GetDevicesTaskResult>("$getDevicesTopic/out")

    mqtt.subscribe<SearchDevicesTask>("+/search/in") { topic, request ->
        val user = topic.substringBefore('/')

        val response = try {
            val savedDevicesIds = (sendAsync(redis,
                "$getSavedDevicesTopic/in",
                GetSavedDevicesTask(user = user)
            ).await() as GetSavedDevicesTaskResult)
                .devices.map { it.id }

            val unknownDevices = (sendAsync(redis,
                "$getDevicesTopic/in",
                GetDevicesTask(user = user, exclude = savedDevicesIds)
            ).await() as GetDevicesTaskResult)
                .devices

            SearchDevicesTaskResult(request.id, devices = unknownDevices ?: emptyList())
        } catch (e: TimeoutException) {
            SearchDevicesTaskResult(request.id, 1, "timeout occurred, try again later")
        }

        mqtt.publish("${user}/search/out", response)
    }

    log.info("Search device handler configured")
}