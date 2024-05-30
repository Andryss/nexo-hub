package org.vivlaniv.nexohub.mobile.util

import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.android.service.MqttAndroidClient
import org.vivlaniv.nexohub.mobile.TAG

inline fun <reified T> MqttAndroidClient.subscribe(
    topic: String,
    crossinline onReceived: (T) -> Unit
) {
    subscribe(topic, 2) { tpc, msg ->
        Log.i(TAG, "mqtt got message on topic $tpc: $msg")
        onReceived(Json.decodeFromString<T>(msg.payload.decodeToString()))
    }
}

inline fun <reified T> MqttAndroidClient.publish(topic: String, message: T) {
    val msg = Json.encodeToString(message)
    Log.i(TAG, "mqtt send message on topic $topic: $msg")
    publish(topic, msg.encodeToByteArray(), 2, false)
}