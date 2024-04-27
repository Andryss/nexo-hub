package org.vivlaniv.nexohub

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future

val waitResponse = ConcurrentHashMap<String, CompletableFuture<TaskResult>>()

inline fun <reified T: Task> sendAsync(client: RedissonClient, topic: String, task: T) : Future<TaskResult> {
    val resultFuture = CompletableFuture<TaskResult>()
    waitResponse[task.id] = resultFuture
    client.getTopic(topic).publish(Json.encodeToString(task))
    return resultFuture
}

inline fun <reified T: Task> sendAsyncNoWait(client: RedissonClient, topic: String, task: T) {
    client.getTopic(topic).publish(Json.encodeToString(task))
}

inline fun <reified T: TaskResult> completeAsync(msg: String) {
    val result = Json.decodeFromString<T>(msg)
    waitResponse.remove(result.tid)?.complete(result)
}

fun main() {
    val log = LoggerFactory.getLogger("connection-service")
    log.info("Starting connection-service")

    val prop = Properties().also {
        it.load(ClassLoader.getSystemResourceAsStream("app.properties"))
    }

    // get properties
    val redisUrl = prop.getProperty("redis.url", "redis://localhost:6379")
    val getDevicesTopic = prop.getProperty("topic.get.devices", "devices/get")
    val getDeviceTopic = prop.getProperty("topic.get.device", "device/get")
    val getSavedDevicesTopic = prop.getProperty("topic.saved.devices", "devices/saved/get")
    val saveDeviceTopic = prop.getProperty("topic.save.device", "devices/save")
    val getDevicesPropertiesTopic = prop.getProperty("topic.get.devices.properties", "devices/properties/get")
    val saveSensorsTopic = prop.getProperty("topic.save.sensors", "sensors/save")
    val setDevicePropertyTopic = prop.getProperty("topic.set.device.property", "device/property/set")
    val signalDeviceTopic = prop.getProperty("topic.signal.device", "device/signal")
    val mqttUrl = prop.getProperty("mqtt.url", "tcp://localhost:1883")

    log.info("Properties loaded")

    // create redis client
    val redisConfig = Config()
    redisConfig.useSingleServer().address = redisUrl
    val redissonClient = Redisson.create(redisConfig)

    // create mqtt client
    val mqttOptions = MqttConnectOptions()
    mqttOptions.isAutomaticReconnect = true
    mqttOptions.isCleanSession = true
    mqttOptions.connectionTimeout = 10
    val mqttClient = MqttClient(mqttUrl, MqttClient.generateClientId(), MemoryPersistence())
    mqttClient.connect(mqttOptions)

    // subscribe on redis topic
    redissonClient.getTopic("$getDevicesTopic/out").addListener(String::class.java) { _, msg ->
        log.info("got get devices response {}", msg)
        completeAsync<GetDevicesTaskResult>(msg)
    }

    redissonClient.getTopic("$getDeviceTopic/out").addListener(String::class.java) { _, msg ->
        log.info("got get device response {}", msg)
        completeAsync<GetDeviceTaskResult>(msg)
    }

    redissonClient.getTopic("$getSavedDevicesTopic/out").addListener(String::class.java) { _, msg ->
        log.info("got get saved devices response {}", msg)
        completeAsync<GetSavedDevicesTaskResult>(msg)
    }

    redissonClient.getTopic("$saveDeviceTopic/out").addListener(String::class.java) { _, msg ->
        log.info("got save device response {}", msg)
        completeAsync<SaveUserDeviceTaskResult>(msg)
    }

    redissonClient.getTopic("$getDevicesPropertiesTopic/out").addListener(String::class.java) { _, msg ->
        log.info("got get devices properties response {}", msg)
        completeAsync<GetDevicesPropertiesTaskResult>(msg)
    }

    redissonClient.getTopic("$setDevicePropertyTopic/out").addListener(String::class.java) { _, msg ->
        log.info("got set device property response {}", msg)
        completeAsync<SetDevicePropertyTaskResult>(msg)
    }

    redissonClient.getTopic("$signalDeviceTopic/out").addListener(String::class.java) { _, msg ->
        log.info("got signal device response {}", msg)
        completeAsync<SignalDeviceTaskResult>(msg)
    }

    // subscribe on mosquitto topics
    mqttClient.subscribe("+/search/in", 2) { topic, msg ->
        val user = topic.substringBefore('/')
        log.info("got search device request for {} {}", user, msg)

        val savedDevicesIds = (sendAsync(
            client = redissonClient,
            topic = "$getSavedDevicesTopic/in",
            task = GetSavedDevicesTask(user = user)
        ).get() as GetSavedDevicesTaskResult)
            .devices.map { it.id }

        val unknownDevices = (sendAsync(
            client = redissonClient,
            topic = "$getDevicesTopic/in",
            task = GetDevicesTask(user = user, exclude = savedDevicesIds)
        ).get() as GetDevicesTaskResult)
            .devices

        val request = Json.decodeFromString<SearchDevicesTask>(msg.payload.decodeToString())
        val response = SearchDevicesTaskResult(request.id, unknownDevices ?: emptyList())
        val responsePayload = Json.encodeToString(response).encodeToByteArray()
        mqttClient.publish("${user}/search/out", responsePayload, 2, false)
    }

    mqttClient.subscribe("+/save/in", 2) { topic, msg ->
        val user = topic.substringBefore('/')
        log.info("got save device request for {} {}", user, msg)
        val request = Json.decodeFromString<SaveDeviceTask>(msg.payload.decodeToString())

        val getDeviceResult = sendAsync(
            client = redissonClient,
            topic = "$getDeviceTopic/in",
            task = GetDeviceTask(user = user, device = request.device)
        ).get() as GetDeviceTaskResult

        val response = if (getDeviceResult.code != 0) {
            SaveDeviceTaskResult(request.id, getDeviceResult.code, getDeviceResult.errorMessage)
        } else {
            val saveDeviceResult = sendAsync(
                client = redissonClient,
                topic = "$saveDeviceTopic/in",
                task = SaveUserDeviceTask(
                    type = getDeviceResult.device!!.type,
                    user = user,
                    device = request.device,
                    room = request.room,
                    alias = request.alias
                )
            ).get() as SaveUserDeviceTaskResult

            SaveDeviceTaskResult(request.id, saveDeviceResult.code, saveDeviceResult.errorMessage)
        }
        val responsePayload = Json.encodeToString(response).encodeToByteArray()
        mqttClient.publish("${user}/save/out", responsePayload, 2, false)
    }

    mqttClient.subscribe("+/fetch/devices/in", 2) { topic, msg ->
        val user = topic.substringBefore('/')
        log.info("got fetch saved devices request for {} {}", user, msg)
        val request = Json.decodeFromString<FetchSavedDevicesTask>(msg.payload.decodeToString())
        val response = run {
            val savedDevices = (sendAsync(redissonClient, "$getSavedDevicesTopic/in",
                GetSavedDevicesTask(user = user))
                .get() as GetSavedDevicesTaskResult).devices
            val savedDevicesMap = mapOf(*savedDevices.map { it.id to it }.toTypedArray())
            val getDevicesResult = (sendAsync(redissonClient, "$getDevicesTopic/in",
                GetDevicesTask(user = user, include = savedDevicesMap.keys.toList()))
                .get() as GetDevicesTaskResult)
            if (getDevicesResult.code != 0) {
                FetchSavedDevicesTaskResult(tid = request.id, code = getDevicesResult.code, errorMessage = getDevicesResult.errorMessage)
            }
            val devices = getDevicesResult.devices!!
            sendAsyncNoWait(redissonClient, "$saveSensorsTopic/in",
                SaveSensorsTask(sensors = mapOf(*devices.map { it.id to it.properties }.toTypedArray())))
            val savedDevicesResponse = devices.map { SavedDevice(it.id, it.type,
                    savedDevicesMap[it.id]?.room, savedDevicesMap[it.id]?.alias, it.properties, it.signals) }
            FetchSavedDevicesTaskResult(tid = request.id, devices = savedDevicesResponse)
        }
        val responsePayload = Json.encodeToString(response).encodeToByteArray()
        mqttClient.publish("${user}/fetch/devices/out", responsePayload, 2, false)
    }

    mqttClient.subscribe("+/fetch/props/in", 2) { topic, msg ->
        val user = topic.substringBefore('/')
        log.info("got fetch all devices request for {} {}", user, msg)

        val request = Json.decodeFromString<FetchDevicesPropertiesTask>(msg.payload.decodeToString())
        val savedDevicesIds = (sendAsync(redissonClient, "$getSavedDevicesTopic/in",
            GetSavedDevicesTask(user = user))
            .get() as GetSavedDevicesTaskResult).devices.mapTo(mutableListOf()) { it.id }
        request.include?.let { savedDevicesIds.retainAll(it) }
        val getDevicesPropsResult = (sendAsync(redissonClient, "$getDevicesPropertiesTopic/in",
            GetDevicesPropertiesTask(user = user, include = savedDevicesIds))
            .get() as GetDevicesPropertiesTaskResult)

        val response = if (getDevicesPropsResult.code != 0) {
            FetchDevicesPropertiesTaskResult(request.id, getDevicesPropsResult.code, getDevicesPropsResult.errorMessage)
        } else {
            sendAsyncNoWait(
                client = redissonClient,
                topic = "$saveSensorsTopic/in",
                task = SaveSensorsTask(sensors = getDevicesPropsResult.properties!!)
            )
            FetchDevicesPropertiesTaskResult(tid = request.id, properties = getDevicesPropsResult.properties)
        }
        val responsePayload = Json.encodeToString(response).encodeToByteArray()
        mqttClient.publish("${user}/fetch/props/out", responsePayload, 2, false)
    }

    mqttClient.subscribe("+/property/in", 2) { topic, msg ->
        val user = topic.substringBefore('/')
        log.info("got put device property request for {} {}", user, msg)

        val request = Json.decodeFromString<PutDevicePropertyTask>(msg.payload.decodeToString())
        val savedDevicesIds = (sendAsync(redissonClient, "$getSavedDevicesTopic/in",
            GetSavedDevicesTask(user = user))
            .get() as GetSavedDevicesTaskResult).devices.map { it.id }

        val device = request.device
        val response = if (device !in savedDevicesIds) {
            PutDevicePropertyTaskResult(tid = request.id, code = 1, errorMessage = "device $device not found", device = device)
        } else {
            val setDevicePropResult = sendAsync(
                client = redissonClient,
                topic = "$setDevicePropertyTopic/in",
                task = SetDevicePropertyTask(
                    user = user,
                    device = device,
                    name = request.property,
                    value = request.value
                )
            ).get() as SetDevicePropertyTaskResult
            PutDevicePropertyTaskResult(
                tid = request.id, code = setDevicePropResult.code, errorMessage = setDevicePropResult.errorMessage, device = device
            )
        }
        val responsePayload = Json.encodeToString(response).encodeToByteArray()
        mqttClient.publish("${user}/property/out", responsePayload, 2, false)
    }

    mqttClient.subscribe("+/signal/in", 2) { topic, msg ->
        val user = topic.substringBefore('/')
        log.info("got send device signal request for {} {}", user, msg)
        val request = Json.decodeFromString<SendDeviceSignalTask>(msg.payload.decodeToString())
        val savedDevicesIds = (sendAsync(
            client = redissonClient,
            topic = "$getSavedDevicesTopic/in",
            task = GetSavedDevicesTask(user = user)
        ).get() as GetSavedDevicesTaskResult)
            .devices.map { it.id }

        val response = if (request.device !in savedDevicesIds) {
            SendDeviceSignalTaskResult(tid = request.id, code = 1, errorMessage = "device ${request.device} not found")
        } else {
            val signalDeviceResult = sendAsync(
                client = redissonClient,
                topic = "$signalDeviceTopic/in",
                task = SignalDeviceTask(user = user, device = request.device, name = request.signal, args = request.arguments)
            ).get() as SignalDeviceTaskResult
            SendDeviceSignalTaskResult(tid = request.id, code = signalDeviceResult.code, errorMessage = signalDeviceResult.errorMessage)
        }
        val responsePayload = Json.encodeToString(response).encodeToByteArray()
        mqttClient.publish("${user}/signal/out", responsePayload, 2, false)
    }

    log.info("connection-service started")
}
