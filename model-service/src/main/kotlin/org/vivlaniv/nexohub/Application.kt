package org.vivlaniv.nexohub

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.redisson.Redisson
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import java.util.*

fun main() {
    val log = LoggerFactory.getLogger("model-service")
    log.info("Starting model-service")

    val prop = Properties()
    prop.load(ClassLoader.getSystemResourceAsStream("app.properties"))

    // get properties
    val redisUrl = prop.getProperty("redis.url", "redis://localhost:6379")
    val getDevicesTopic = prop.getProperty("topic.get.devices", "devices/get")
    val getDeviceTopic = prop.getProperty("topic.get.device", "device/get")
    val getDevicesPropertiesTopic = prop.getProperty("topic.get.devices.properties", "devices/properties/get")
    val setDevicePropertyTopic = prop.getProperty("topic.set.device.property", "device/property/set")
    val signalDeviceTopic = prop.getProperty("topic.signal.device", "device/signal")

    log.info("Properties loaded")

    val userToDevices: MutableMap<String, MutableMap<String, Device>> = mutableMapOf(
        "user" to mutableMapOf(
            Lamp().let { it.getId() to it },
            Teapot().let { it.getId() to it }
        )
    )

    log.info("Model initialized")

    // create redis client
    val redisConfig = Config()
    redisConfig.useSingleServer().address = redisUrl
    val redissonClient = Redisson.create(redisConfig)

    // subscribe on topics
    redissonClient.getTopic("$getDevicesTopic/in").addListener(String::class.java) { _, msg ->
        log.info("got get devices request {}", msg)
        val task = Json.decodeFromString<GetDevicesTask>(msg)
        val devices = userToDevices[task.user]
        val result = if (devices == null) {
            GetDevicesTaskResult(tid = task.id, code = 1, errorMessage = "user ${task.user} not found")
        } else {
            var infos = devices.values.map { it.getInfo() }
            if (task.include != null) infos = infos.filter { task.include!!.contains(it.id) }
            if (task.exclude != null) infos = infos.filter { !task.exclude!!.contains(it.id) }
            GetDevicesTaskResult(tid = task.id, devices = infos)
        }
        redissonClient.getTopic("$getDevicesTopic/out").publish(Json.encodeToString(result))
    }

    redissonClient.getTopic("$getDeviceTopic/in").addListener(String::class.java) { _, msg ->
        log.info("got get device request {}", msg)
        val task = Json.decodeFromString<GetDeviceTask>(msg)
        val devices = userToDevices[task.user]
        val result = if (devices == null) {
            GetDeviceTaskResult(tid = task.id, code = 1, errorMessage = "user ${task.user} not found")
        } else {
            val device = devices[task.device]
            if (device == null) {
                GetDeviceTaskResult(tid = task.id, code = 2, errorMessage = "device ${task.device} not found")
            } else {
                GetDeviceTaskResult(tid = task.id, device = device.getInfo())
            }
        }
        redissonClient.getTopic("$getDeviceTopic/out").publish(Json.encodeToString(result))
    }

    redissonClient.getTopic("$getDevicesPropertiesTopic/in").addListener(String::class.java) { _, msg ->
        log.info("got get devices properties request {}", msg)
        val task = Json.decodeFromString<GetDevicesPropertiesTask>(msg)
        val devices = userToDevices[task.user]
        val result = if (devices == null) {
            GetDevicesPropertiesTaskResult(tid = task.id, code = 1, errorMessage = "user ${task.user} not found")
        } else {
            var properties = devices.mapValues { it.value.getProperties() }
            task.include?.run {
                properties = properties.filterKeys { this.contains(it) }
            }
            GetDevicesPropertiesTaskResult(tid =  task.id, properties = properties)
        }
        redissonClient.getTopic("$getDevicesPropertiesTopic/out").publish(Json.encodeToString(result))
    }

    redissonClient.getTopic("$setDevicePropertyTopic/in").addListener(String::class.java) { _, msg ->
        log.info("got set device property request {}", msg)
        val task = Json.decodeFromString<SetDevicePropertyTask>(msg)
        val devices = userToDevices[task.user]
        val result = if (devices == null) {
            SetDevicePropertyTaskResult(tid = task.id, code = 1, errorMessage = "user ${task.user} not found")
        } else {
            val device = devices[task.device]
            if (device == null) {
                SetDevicePropertyTaskResult(tid = task.id, code = 2, errorMessage = "device ${task.device} not found")
            } else {
                device.setProperty(task.name, task.value)
                SetDevicePropertyTaskResult(task.id)
            }
        }
        redissonClient.getTopic("$setDevicePropertyTopic/out").publish(Json.encodeToString(result))
    }

    redissonClient.getTopic("$signalDeviceTopic/in").addListener(String::class.java) { _, msg ->
        log.info("got signal device request {}", msg)
        val task = Json.decodeFromString<SignalDeviceTask>(msg)
        val devices = userToDevices[task.user]
        val result = if (devices == null) {
            SignalDeviceTaskResult(tid = task.id, code = 1, errorMessage = "user ${task.user} not found")
        } else {
            val device = devices[task.device]
            if (device == null) {
                SignalDeviceTaskResult(tid = task.id, code = 2, errorMessage = "device ${task.device} not found")
            } else {
                device.signal(task.name, task.args)
                SignalDeviceTaskResult(task.id)
            }
        }
        redissonClient.getTopic("$signalDeviceTopic/out").publish(Json.encodeToString(result))
    }

    log.info("model-service started")
}
