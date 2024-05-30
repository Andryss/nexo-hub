package org.vivlaniv.nexohub.connection

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.vivlaniv.nexohub.connection.handlers.configureFetchAllPropertiesHandler
import org.vivlaniv.nexohub.connection.handlers.configureFetchSavedDevicesHandler
import org.vivlaniv.nexohub.connection.handlers.configurePutDevicePropertyHandler
import org.vivlaniv.nexohub.connection.handlers.configureSaveDeviceHandler
import org.vivlaniv.nexohub.connection.handlers.configureSearchDeviceHandler
import org.vivlaniv.nexohub.connection.handlers.configureSignInHandler
import org.vivlaniv.nexohub.connection.handlers.configureSignUpHandler
import java.util.Properties

class Application(
    val log: Logger,
    val props: Properties,
    val redis: RedissonClient,
    val mqtt: MqttClient
)

fun main() {
    val log = LoggerFactory.getLogger("connection-service")
    log.info("Starting connection-service")

    // load properties
    val props = Properties().also {
        it.load(ClassLoader.getSystemResourceAsStream("app.properties"))
    }

    log.info("Properties loaded")

    // create redis client
    val redis = Redisson.create(
        Config().apply {
            useSingleServer().address = props.getProperty("redis.url", "redis://redis:6379")
        }
    )

    // create mqtt client
    val mqtt = MqttClient(
        props.getProperty("mqtt.url", "tcp://mosquitto:1883"),
        MqttClient.generateClientId(),
        MemoryPersistence()
    ).also {
        it.connect(
            MqttConnectOptions().apply {
                isAutomaticReconnect = true
                isCleanSession = true
                connectionTimeout = 10
                maxInflight = 128
            }
        )
    }

    // configure handlers
    Application(log, props, redis, mqtt).apply {
        configureSignInHandler()
        configureSignUpHandler()
        configureSearchDeviceHandler()
        configureSaveDeviceHandler()
        configureFetchSavedDevicesHandler()
        configureFetchAllPropertiesHandler()
        configurePutDevicePropertyHandler()
    }

    log.info("connection-service started")
}
