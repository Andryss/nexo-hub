package org.vivlaniv.nexohub

import kotlinx.coroutines.runBlocking
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.vivlaniv.nexohub.device.Device
import org.vivlaniv.nexohub.device.Humidifier
import org.vivlaniv.nexohub.device.Lamp
import org.vivlaniv.nexohub.device.Socket
import org.vivlaniv.nexohub.device.THSensor
import org.vivlaniv.nexohub.device.Teapot
import org.vivlaniv.nexohub.device.Thermostat
import org.vivlaniv.nexohub.handlers.configureGetDeviceHandler
import org.vivlaniv.nexohub.handlers.configureGetDevicesHandler
import org.vivlaniv.nexohub.handlers.configureGetDevicesPropertiesHandler
import org.vivlaniv.nexohub.handlers.configureSetDevicePropertyHandler
import org.vivlaniv.nexohub.util.coroutines.launchWithFixedDelay
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class Application(
    val log: Logger,
    val props: Properties,
    val redis: RedissonClient,
    val userToDevices: MutableMap<String, MutableMap<String, Device>>,
    val houses: MutableList<House>
)

fun main() = runBlocking {
    val log = LoggerFactory.getLogger("model-service")
    log.info("Starting model-service")

    // load properties
    val props = Properties().also {
        it.load(ClassLoader.getSystemResourceAsStream("app.properties"))
    }

    log.info("Properties loaded")

    // initialize model
    val houses = CopyOnWriteArrayList<House>().also {
        it.add(
            House(
                id = "Kronverksky Prospekt 49",
                owner = "demo",
                rooms = listOf(
                    Room(devices = listOf(Lamp(), Teapot(), Socket(), Thermostat(), Humidifier(), THSensor()))
                )
            )
        )
    }

    val userToDevices: MutableMap<String, MutableMap<String, Device>> = ConcurrentHashMap()
    houses.forEach { house ->
        userToDevices[house.owner] = mutableMapOf(
            *house.rooms.flatMap { it.devices }.map { it.getId() to it }.toTypedArray()
        )
    }

    World(houses).apply {
        launchWithFixedDelay(60_000) { doWeatherAction() }
        launchWithFixedDelay(10_000) { keepHeatableTemperature() }
        launchWithFixedDelay(15_000) { doEvaporate() }
        launchWithFixedDelay(5_000) { followHeatersTemperature() }
        launchWithFixedDelay(5_000) { followWettorsHumidity() }
        launchWithFixedDelay(10_000) { putSensorValues() }
        launchWithFixedDelay(1_000) { changeTeapotTemperature() }
    }

    log.info("Model initialized")

    // create redis client
    val redis = Redisson.create(
        Config().apply {
            useSingleServer().address = props.getProperty("redis.url", "redis://redis:6379")
        }
    )

    // configure handlers
    Application(log, props, redis, userToDevices, houses).apply {
        configureGetDevicesHandler()
        configureGetDeviceHandler()
        configureGetDevicesPropertiesHandler()
        configureSetDevicePropertyHandler()
    }

    log.info("model-service started")
}
