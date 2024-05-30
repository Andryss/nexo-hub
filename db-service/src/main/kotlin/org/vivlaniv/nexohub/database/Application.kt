package org.vivlaniv.nexohub.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.vivlaniv.nexohub.database.handlers.configureFindUserHandler
import org.vivlaniv.nexohub.database.handlers.configureGetSavedDevicesHandler
import org.vivlaniv.nexohub.database.handlers.configureSaveDeviceHandler
import org.vivlaniv.nexohub.database.handlers.configureSaveSensorsHandler
import org.vivlaniv.nexohub.database.handlers.configureSaveUserHandler
import java.util.Properties

class Application(
    val log: Logger,
    val props: Properties,
    val redis: RedissonClient
)

fun main() {
    val log = LoggerFactory.getLogger("db-service")

    log.info("Starting db-service")

    // load properties
    val props = Properties().also {
        it.load(ClassLoader.getSystemResourceAsStream("app.properties"))
    }

    log.info("Properties loaded")

    // create redis client
    val redisClient = Redisson.create(
        Config().apply {
            useSingleServer().address = props.getProperty("redis.url", "redis://redis:6379")
        }
    )

    // connect to database
    Database.connect(
        HikariDataSource(HikariConfig().apply {
            jdbcUrl = props.getProperty("datasource.url", "jdbc:postgresql://postgres:5432/postgres")
            driverClassName = props.getProperty("datasource.driver", "org.postgresql.Driver")
            username = props.getProperty("datasource.user", "postgres")
            password = props.getProperty("datasource.pass", "postgres")
        })
    )

    transaction {
        if (props.getProperty("tables.drop-first", "false").toBoolean()) {
            SchemaUtils.drop(Users, Devices, SensorRecords)
        }
        SchemaUtils.createMissingTablesAndColumns(Users, Devices, SensorRecords)
    }

    log.info("Database initialized")

    // configure handlers
    Application(log, props, redisClient).apply {
        configureFindUserHandler()
        configureSaveUserHandler()
        configureSaveDeviceHandler()
        configureGetSavedDevicesHandler()
        configureSaveSensorsHandler()
    }

    log.info("db-service started")
}

