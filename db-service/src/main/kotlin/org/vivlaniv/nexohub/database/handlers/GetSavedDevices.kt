package org.vivlaniv.nexohub.database.handlers

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.vivlaniv.nexohub.common.SavedDeviceInfo
import org.vivlaniv.nexohub.common.task.GetSavedDevicesTask
import org.vivlaniv.nexohub.common.task.GetSavedDevicesTaskResult
import org.vivlaniv.nexohub.common.util.redis.publish
import org.vivlaniv.nexohub.common.util.redis.subscribe
import org.vivlaniv.nexohub.database.Application
import org.vivlaniv.nexohub.database.Devices

fun Application.configureGetSavedDevicesHandler() {
    val getSavedDevicesTopic = props.getProperty("topic.saved.devices", "devices/saved/get")

    redis.subscribe<GetSavedDevicesTask>("$getSavedDevicesTopic/in") { task ->
        val devices = transaction {
            Devices.selectAll()
                .where { Devices.user eq task.user }
                .map {
                    SavedDeviceInfo(
                        it[Devices.id],
                        it[Devices.type],
                        it[Devices.user],
                        it[Devices.room],
                        it[Devices.alias]
                    )
                }
        }
        val result = GetSavedDevicesTaskResult(task.id, devices)
        redis.publish("$getSavedDevicesTopic/out", result)
    }

    log.info("Get saved devices handler configured")
}