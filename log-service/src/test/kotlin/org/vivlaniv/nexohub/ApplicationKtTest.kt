package org.vivlaniv.nexohub

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.redisson.Redisson
import org.redisson.config.Config
import java.time.Instant

fun main() = runBlocking {
    val config = Config()
    config.useSingleServer().address = "redis://redis:6379"
    val redissonClient = Redisson.create(config)
    for (i in 0 until 20) {
        launch(Dispatchers.IO) {
            for (j in 0 until 100000) {
                redissonClient.getTopic("logs").publish(Json.encodeToString(
                    LogMessage(
                        Instant.now().toEpochMilli(),
                        "some-service",
                        "INFO",
                        "some-thread",
                        "some-logger",
                        genMsg(),
                        null
                    )
                ))
            }
            println("thread finished")
        }
    }

    println("all workers launched")
}

private fun genMsg(): String {
    val chars = CharArray(1024)
    for (i in chars.indices) {
        chars[i] = 'a' + (Math.random() * 26).toInt()
    }
    return String(chars)
}