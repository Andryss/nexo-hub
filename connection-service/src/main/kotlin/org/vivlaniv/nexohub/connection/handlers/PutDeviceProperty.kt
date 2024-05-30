package org.vivlaniv.nexohub.connection.handlers

import org.vivlaniv.nexohub.common.task.*
import org.vivlaniv.nexohub.common.util.mqtt.publish
import org.vivlaniv.nexohub.common.util.mqtt.subscribe
import org.vivlaniv.nexohub.connection.Application
import org.vivlaniv.nexohub.connection.util.requireAsyncCompleter
import org.vivlaniv.nexohub.connection.util.sendAsync
import java.util.concurrent.TimeoutException

fun Application.configurePutDevicePropertyHandler() {
    val getSavedDevicesTopic = props.getProperty("topic.saved.devices", "devices/saved/get")
    val setDevicePropertyTopic = props.getProperty("topic.set.device.property", "device/property/set")

    redis.requireAsyncCompleter<GetSavedDevicesTaskResult>("$getSavedDevicesTopic/out")
    redis.requireAsyncCompleter<SetDevicePropertyTaskResult>("$setDevicePropertyTopic/out")

    mqtt.subscribe<PutDevicePropertyTask>("+/property/in") { topic, request ->
        val user = topic.substringBefore('/')

        val device = request.device

        val response = try {
            val savedDevicesIds = (sendAsync(redis,
                "$getSavedDevicesTopic/in",
                GetSavedDevicesTask(user = user)
            ).await() as GetSavedDevicesTaskResult)
                .devices.map { it.id }

            if (device !in savedDevicesIds) {
                PutDevicePropertyTaskResult(request.id, 1, "device $device not found", device)
            } else {
                val setDevicePropResult = sendAsync(redis,
                    "$setDevicePropertyTopic/in",
                    SetDevicePropertyTask(
                        user = user,
                        device = device,
                        name = request.property,
                        value = request.value
                    )
                ).await() as SetDevicePropertyTaskResult

                PutDevicePropertyTaskResult(request.id, setDevicePropResult.code, setDevicePropResult.errorMessage, device)
            }
        } catch (e: TimeoutException) {
            PutDevicePropertyTaskResult(request.id, 1, "timeout occurred, try again later", device)
        }

        mqtt.publish("${user}/property/out", response)
    }

    log.info("Put device property handler configured")
}