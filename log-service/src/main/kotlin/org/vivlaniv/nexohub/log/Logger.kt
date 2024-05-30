package org.vivlaniv.nexohub.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxy
import ch.qos.logback.core.AppenderBase
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import java.io.CharArrayWriter
import java.io.PrintWriter

@Serializable
data class LogMessage(
    val timestamp: Long,
    val service: String,
    val level: String,
    val thread: String,
    val logger: String,
    val message: String,
    val trace: String?
)

@Suppress("MemberVisibilityCanBePrivate")
class LogServiceAppender: AppenderBase<ILoggingEvent>() {

    var redisUrl: String = "redis://redis:6379"
    var logsTopic: String = "logs"
    var serviceName: String = "unnamed"

    private var redissonClient: RedissonClient? = null

    override fun start() {
        super.start()
        if (redissonClient == null) {
            val redisConfig = Config()
            redisConfig.useSingleServer().address = redisUrl
            redissonClient = Redisson.create(redisConfig)
        }
    }

    override fun append(event: ILoggingEvent) {
        redissonClient?.getTopic(logsTopic)?.publish(Json.encodeToString(
            LogMessage(
                event.timeStamp,
                serviceName,
                event.level.levelStr,
                event.threadName,
                event.loggerName,
                event.formattedMessage,
                event.throwableProxy?.let {
                    val writer = CharArrayWriter()
                    (it as ThrowableProxy).throwable.printStackTrace(PrintWriter(writer))
                    writer.toString()
                }
            )
        ))
    }
}