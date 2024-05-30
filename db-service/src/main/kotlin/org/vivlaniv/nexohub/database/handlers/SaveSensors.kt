package org.vivlaniv.nexohub.database.handlers

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.vivlaniv.nexohub.common.task.SaveSensorsTask
import org.vivlaniv.nexohub.common.util.redis.subscribe
import org.vivlaniv.nexohub.database.Application
import org.vivlaniv.nexohub.database.SensorRecords
import java.time.Instant

fun Application.configureSaveSensorsHandler() {
    val saveSensorsTopic = props.getProperty("topic.save.sensors", "sensors/save")

    redis.subscribe<SaveSensorsTask>("$saveSensorsTopic/in") { task ->
        val now = Instant.now()
        transaction {
            for (deviceSensors in task.sensors) {
                for (sensorRecord in deviceSensors.value) {
                    SensorRecords.insert {
                        it[device] = deviceSensors.key
                        it[sensor] = sensorRecord.name
                        it[value] = sensorRecord.value
                        it[timestamp] = now
                    }
                }
            }
        }
    }

    log.info("Save sensors handler configured")
}