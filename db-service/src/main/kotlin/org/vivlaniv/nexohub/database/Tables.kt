package org.vivlaniv.nexohub.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Users : Table("users") {
    val username = text("username")
    val password = text("password")
    override val primaryKey = PrimaryKey(username)
}

object Devices : Table("devices") {
    val id = text("id")
    val type = text("type")
    val user = text("user")
    val room = text("root").nullable()
    val alias = text("alias").nullable()
    override val primaryKey = PrimaryKey(id)
}

object SensorRecords : Table("sensor_records") {
    val device = text("device")
    val sensor = text("sensor")
    val value = integer("value")
    val timestamp = timestamp("timestamp")
}
