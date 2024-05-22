package org.vivlaniv.nexohub.handlers

import org.vivlaniv.nexohub.Application
import org.vivlaniv.nexohub.GetSavedDevicesTask
import org.vivlaniv.nexohub.GetSavedDevicesTaskResult
import org.vivlaniv.nexohub.PutDevicePropertyTask
import org.vivlaniv.nexohub.PutDevicePropertyTaskResult
import org.vivlaniv.nexohub.SetDevicePropertyTask
import org.vivlaniv.nexohub.SetDevicePropertyTaskResult
import org.vivlaniv.nexohub.util.async.requireAsyncCompleter
import org.vivlaniv.nexohub.util.async.sendAsync
import org.vivlaniv.nexohub.util.mqtt.publish
import org.vivlaniv.nexohub.util.mqtt.subscribe
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