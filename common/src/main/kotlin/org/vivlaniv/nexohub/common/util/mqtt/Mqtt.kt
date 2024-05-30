package org.vivlaniv.nexohub.common.util.mqtt

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

val mqttLogger: Logger = LoggerFactory.getLogger("mqtt")

val mqttScope = CoroutineScope(Executors.newFixedThreadPool(32).asCoroutineDispatcher())

inline fun <reified T> MqttClient.subscribe(
    topic: String,
    crossinline onReceived: suspend CoroutineScope.(String, T) -> Unit
) {
    subscribe(topic, 2) { tpc, msg ->
        mqttScope.launch {
            mqttLogger.info("got message on topic $tpc: $msg")
            onReceived(tpc, Json.decodeFromString<T>(msg.payload.decodeToString()))
        }
    }
}

inline fun <reified T> MqttClient.publish(topic: String, message: T) {
    val msg = Json.encodeToString(message)
    mqttLogger.info("send message on topic $topic: $msg")
    publish(topic, msg.encodeToByteArray(), 2, false)
}