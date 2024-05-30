package org.vivlaniv.nexohub.model.handlers

import org.vivlaniv.nexohub.model.Application
import org.vivlaniv.nexohub.common.task.GetDevicesPropertiesTask
import org.vivlaniv.nexohub.common.task.GetDevicesPropertiesTaskResult
import org.vivlaniv.nexohub.common.util.redis.publish
import org.vivlaniv.nexohub.common.util.redis.subscribe

fun Application.configureGetDevicesPropertiesHandler() {
    val getDevicesPropertiesTopic = props.getProperty("topic.get.devices.properties", "devices/properties/get")

    redis.subscribe<GetDevicesPropertiesTask>("$getDevicesPropertiesTopic/in") { task ->
        val devices = userToDevices[task.user] ?: mapOf()
        var properties = devices.mapValues { it.value.getProperties() }
        task.include?.let { include -> properties = properties.filterKeys { include.contains(it) } }
        val result = GetDevicesPropertiesTaskResult(tid =  task.id, properties = properties)
        redis.publish("$getDevicesPropertiesTopic/out", result)
    }

    log.info("Get devices properties handler configured")
}