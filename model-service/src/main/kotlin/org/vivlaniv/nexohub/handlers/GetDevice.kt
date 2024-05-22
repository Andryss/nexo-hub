package org.vivlaniv.nexohub.handlers

import org.vivlaniv.nexohub.Application
import org.vivlaniv.nexohub.GetDeviceTask
import org.vivlaniv.nexohub.GetDeviceTaskResult
import org.vivlaniv.nexohub.util.redis.publish
import org.vivlaniv.nexohub.util.redis.subscribe

fun Application.configureGetDeviceHandler() {
    val getDeviceTopic = props.getProperty("topic.get.device", "device/get")

    redis.subscribe<GetDeviceTask>("$getDeviceTopic/in") { task ->
        val devices = userToDevices[task.user]
        val result = if (devices == null) {
            GetDeviceTaskResult(tid = task.id, code = 1, errorMessage = "user ${task.user} not found")
        } else {
            val device = devices[task.device]
            if (device == null) {
                GetDeviceTaskResult(tid = task.id, code = 2, errorMessage = "device ${task.device} not found")
            } else {
                GetDeviceTaskResult(tid = task.id, device = device.getInfo())
            }
        }
        redis.publish("$getDeviceTopic/out", result)
    }

    log.info("Get device handler configured")
}