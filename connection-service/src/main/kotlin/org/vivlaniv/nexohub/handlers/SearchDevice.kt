package org.vivlaniv.nexohub.handlers

import org.vivlaniv.nexohub.Application
import org.vivlaniv.nexohub.GetDevicesTask
import org.vivlaniv.nexohub.GetDevicesTaskResult
import org.vivlaniv.nexohub.GetSavedDevicesTask
import org.vivlaniv.nexohub.GetSavedDevicesTaskResult
import org.vivlaniv.nexohub.SearchDevicesTask
import org.vivlaniv.nexohub.SearchDevicesTaskResult
import org.vivlaniv.nexohub.util.async.requireAsyncCompleter
import org.vivlaniv.nexohub.util.async.sendAsync
import org.vivlaniv.nexohub.util.mqtt.publish
import org.vivlaniv.nexohub.util.mqtt.subscribe
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