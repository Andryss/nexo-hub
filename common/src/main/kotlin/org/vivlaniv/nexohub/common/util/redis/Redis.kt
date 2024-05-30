package org.vivlaniv.nexohub.common.util.redis

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory


val redisLogger: Logger = LoggerFactory.getLogger("redis")

inline fun <reified T> RedissonClient.subscribe(topic: String, crossinline onReceived: (T) -> Unit) {
    getTopic(topic).addListener(String::class.java) { tpc, msg ->
        redisLogger.info("got message on topic $tpc: $msg")
        onReceived(Json.decodeFromString<T>(msg))
    }
}

inline fun <reified T> RedissonClient.publish(topic: String, message: T) {
    val msg = Json.encodeToString(message)
    redisLogger.info("send message on topic $topic: $msg")
    getTopic(topic).publish(msg)
}