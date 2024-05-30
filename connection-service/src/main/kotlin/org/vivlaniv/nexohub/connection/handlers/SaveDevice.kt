package org.vivlaniv.nexohub.connection.handlers

import org.vivlaniv.nexohub.common.task.*
import org.vivlaniv.nexohub.common.util.mqtt.publish
import org.vivlaniv.nexohub.common.util.mqtt.subscribe
import org.vivlaniv.nexohub.connection.Application
import org.vivlaniv.nexohub.connection.util.requireAsyncCompleter
import org.vivlaniv.nexohub.connection.util.sendAsync
import java.util.concurrent.TimeoutException

fun Application.configureSaveDeviceHandler() {
    val getDeviceTopic = props.getProperty("topic.get.device", "device/get")
    val saveDeviceTopic = props.getProperty("topic.save.device", "devices/save")

    redis.requireAsyncCompleter<GetDeviceTaskResult>("$getDeviceTopic/out")
    redis.requireAsyncCompleter<SaveUserDeviceTaskResult>("$saveDeviceTopic/out")

    mqtt.subscribe<SaveDeviceTask>("+/save/in") { topic, request ->
        val user = topic.substringBefore('/')

        val response = try {
            val getDeviceResult = sendAsync(redis,
                "$getDeviceTopic/in",
                GetDeviceTask(user = user, device = request.device)
            ).await() as GetDeviceTaskResult

            if (getDeviceResult.code != 0) {
                SaveDeviceTaskResult(request.id, getDeviceResult.code, getDeviceResult.errorMessage)
            } else {
                val saveDeviceResult = sendAsync(redis,
                    "$saveDeviceTopic/in",
                    SaveUserDeviceTask(
                        type = getDeviceResult.device!!.type,
                        user = user,
                        device = request.device,
                        room = request.room,
                        alias = request.alias
                    )
                ).await() as SaveUserDeviceTaskResult

                SaveDeviceTaskResult(request.id, saveDeviceResult.code, saveDeviceResult.errorMessage)
            }
        } catch (e: TimeoutException) {
            SaveDeviceTaskResult(request.id, 1, "timeout occurred, try again later")
        }

        mqtt.publish("${user}/save/out", response)
    }

    log.info("Save device handler configured")
}