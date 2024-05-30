package org.vivlaniv.nexohub.database.handlers

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.vivlaniv.nexohub.common.task.SaveUserDeviceTask
import org.vivlaniv.nexohub.common.task.SaveUserDeviceTaskResult
import org.vivlaniv.nexohub.common.util.redis.publish
import org.vivlaniv.nexohub.common.util.redis.subscribe
import org.vivlaniv.nexohub.database.Application
import org.vivlaniv.nexohub.database.Devices

fun Application.configureSaveDeviceHandler() {
    val saveDeviceTopic = props.getProperty("topic.save.device", "devices/save")

    redis.subscribe<SaveUserDeviceTask>("$saveDeviceTopic/in") { task ->
        transaction {
            val rows = Devices.update({ (Devices.id eq task.device) and (Devices.user eq task.user) }) {
                it[type] = task.type
                it[room] = task.room
                it[alias] = task.alias
            }
            if (rows == 0) {
                Devices.insert {
                    it[id] = task.device
                    it[type] = task.type
                    it[user] = task.user
                    it[room] = task.room
                    it[alias] = task.alias
                }
            }
        }
        val result = SaveUserDeviceTaskResult(task.id)
        redis.publish("$saveDeviceTopic/out", result)
    }

    log.info("Save device handler configured")
}