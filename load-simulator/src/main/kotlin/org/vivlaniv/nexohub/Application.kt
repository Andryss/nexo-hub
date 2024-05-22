package org.vivlaniv.nexohub

import kotlinx.coroutines.runBlocking
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class Application (
    val log: Logger,
    val props: Properties,
    val mqtt: MqttClient
)

fun main() = runBlocking {
    val log = LoggerFactory.getLogger("load-simulator")

    log.info("Starting load-simulator")

    val props = Properties().also {
        it.load(ClassLoader.getSystemResourceAsStream("app.properties"))
    }

    val mqttClient = MqttClient(
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

    log.info("Configuration completed")

    log.info("Starting load generation")

    Application(log, props, mqttClient).startScenario()

    log.info("Load generation started")
}
