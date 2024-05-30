package org.vivlaniv.nexohub.model.handlers

import org.vivlaniv.nexohub.model.Application
import org.vivlaniv.nexohub.common.task.SetDevicePropertyTask
import org.vivlaniv.nexohub.common.task.SetDevicePropertyTaskResult
import org.vivlaniv.nexohub.common.util.redis.publish
import org.vivlaniv.nexohub.common.util.redis.subscribe

fun Application.configureSetDevicePropertyHandler() {
    val setDevicePropertyTopic = props.getProperty("topic.set.device.property", "device/property/set")

    redis.subscribe<SetDevicePropertyTask>("$setDevicePropertyTopic/in") { task ->
        val devices = userToDevices[task.user]
        val result = if (devices == null) {
            SetDevicePropertyTaskResult(tid = task.id, code = 1, errorMessage = "user ${task.user} not found")
        } else {
            val device = devices[task.device]
            if (device == null) {
                SetDevicePropertyTaskResult(tid = task.id, code = 2, errorMessage = "device ${task.device} not found")
            } else {
                device.setProperty(task.name, task.value)
                SetDevicePropertyTaskResult(task.id)
            }
        }
        redis.publish("$setDevicePropertyTopic/out", result)
    }

    log.info("Set device property handler configured")
}