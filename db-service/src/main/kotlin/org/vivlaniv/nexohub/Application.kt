package org.vivlaniv.nexohub

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.redisson.Redisson
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*

fun main() {
    val log = LoggerFactory.getLogger("db-service")
    log.info("Starting db-service")

    val prop = Properties()
    prop.load(ClassLoader.getSystemResourceAsStream("app.properties"))

    // get properties
    val redisUrl = prop.getProperty("redis.url", "redis://localhost:6379")
    val datasourceUrl = prop.getProperty("datasource.url", "jdbc:postgresql://localhost:5432/postgres")
    val datasourceDriver = prop.getProperty("datasource.driver", "org.postgresql.Driver")
    val datasourceUser = prop.getProperty("datasource.user", "postgres")
    val datasourcePass = prop.getProperty("datasource.pass", "postgres")
    val getSavedDevicesTopic = prop.getProperty("topic.saved.devices", "devices/saved/get")
    val saveDeviceTopic = prop.getProperty("topic.save.device", "devices/save")
    val saveSensorsTopic = prop.getProperty("topic.save.sensors", "sensors/save")

    log.info("Properties loaded")

    // create redis client
    val redisConfig = Config()
    redisConfig.useSingleServer().address = redisUrl
    val redissonClient = Redisson.create(redisConfig)

    // connect to database
    val dataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = datasourceUrl
        driverClassName = datasourceDriver
        username = datasourceUser
        password = datasourcePass
    })
    Database.connect(dataSource)

    transaction {
        SchemaUtils.createMissingTablesAndColumns(Devices, SensorRecords)
    }

    log.info("Database initialized")

    // subscribe on topics
    redissonClient.getTopic("$getSavedDevicesTopic/in").addListener(String::class.java) { _, msg ->
        log.info("got get saved devices request {}", msg)
        val task = Json.decodeFromString<GetSavedDevicesTask>(msg)
        val devices = transaction {
            Devices.selectAll().map {
                SavedDeviceInfo(it[Devices.id], it[Devices.type], it[Devices.user], it[Devices.room], it[Devices.alias])
            }
        }
        val result = GetSavedDevicesTaskResult(task.id, devices)
        redissonClient.getTopic("$getSavedDevicesTopic/out")
            .publish(Json.encodeToString(result))
    }

    redissonClient.getTopic("$saveDeviceTopic/in").addListener(String::class.java) { _, msg ->
        log.info("got save device request {}", msg)
        val task = Json.decodeFromString<SaveUserDeviceTask>(msg)
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
        redissonClient.getTopic("$saveDeviceTopic/out").publish(Json.encodeToString(result))
    }

    redissonClient.getTopic("$saveSensorsTopic/in").addListener(String::class.java) { _, msg ->
        log.info("got save sensors request {}", msg)
        val task = Json.decodeFromString<SaveSensorsTask>(msg)
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

    log.info("db-service started")
}

