package org.vivlaniv.nexohub

import com.clickhouse.client.ClickHouseClient
import com.clickhouse.client.ClickHouseNode
import com.clickhouse.client.ClickHouseProtocol
import com.clickhouse.client.ClickHouseResponse
import com.clickhouse.data.ClickHouseDataStreamFactory
import com.clickhouse.data.ClickHouseFormat
import com.clickhouse.data.format.BinaryStreamUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.redisson.Redisson
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Properties
import java.util.TimeZone
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking {
    val log = LoggerFactory.getLogger("log-service")
    log.info("Starting log-service")

    val prop = Properties().also {
        it.load(ClassLoader.getSystemResourceAsStream("app.properties"))
    }

    // get properties
    val redisUrl = prop.getProperty("redis.url", "redis://redis:6379")
    val logsTopic = prop.getProperty("topic.logs", "logs")
    val chUrl = prop.getProperty("clickhouse.url", "http://clickhouse:8123")
    val logsTable = prop.getProperty("logs.table", "logs")
    val logsDropFirst = prop.getProperty("logs.table.drop-first", "false").toBoolean()

    log.info("Properties loaded")

    // create redis client
    val redisConfig = Config()
    redisConfig.useSingleServer().address = redisUrl
    val redissonClient = Redisson.create(redisConfig)

    // create clickhouse client
    val chNode = ClickHouseNode.of(chUrl)

    ClickHouseClient.newInstance(ClickHouseProtocol.HTTP).use { client ->
        if (logsDropFirst) {
            client.read(chNode).query("drop table if exists $logsTable").execute().get()
        }
        client.read(chNode)
            .query("""
                create table if not exists $logsTable (
                    timestamp DATETIME64,
                    service String,
                    level String,
                    thread String,
                    logger String,
                    message String,
                    trace Nullable(String)
                ) engine MergeTree order by timestamp
            """.trimIndent())
            .execute().get()
    }

    // create insert batch
    val batch = Batch<LogMessage> {
        log.info("Batch of new log messages inserting")
        val writtenRows: Long
        ClickHouseClient.newInstance(ClickHouseProtocol.HTTP).use { client ->
            val request = client.read(chNode).write().table(logsTable).format(ClickHouseFormat.RowBinary)
            val config = request.config
            val future: CompletableFuture<ClickHouseResponse>
            ClickHouseDataStreamFactory.getInstance().createPipedOutputStream(config).use { stream ->
                future = request.data(stream.inputStream).execute()
                for (message in it) {
                    BinaryStreamUtils.writeDateTime64(stream,
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(message.timestamp), ZoneOffset.UTC),
                        TimeZone.getTimeZone(ZoneOffset.UTC))
                    BinaryStreamUtils.writeString(stream, message.service)
                    BinaryStreamUtils.writeString(stream, message.level)
                    BinaryStreamUtils.writeString(stream, message.thread)
                    BinaryStreamUtils.writeString(stream, message.logger)
                    BinaryStreamUtils.writeString(stream, message.message)
                    if (message.trace == null) {
                        BinaryStreamUtils.writeNull(stream)
                    } else {
                        BinaryStreamUtils.writeNonNull(stream)
                        BinaryStreamUtils.writeString(stream, message.trace)
                    }
                }
            }
            writtenRows = future.get().summary.writtenRows
        }
        log.info("Batch of $writtenRows log messages inserted")
    }

    launch(Dispatchers.IO) {
        while (true) {
            delay(30.seconds)
            batch.flush()
        }
    }

    // subscribe on topic
    redissonClient.getTopic(logsTopic).addListener(String::class.java) { _, msg ->
        launch {
            batch.add(Json.decodeFromString(msg))
        }
    }

    log.info("log-service started")
}

class Batch<T>(private val size: Int = 500_000, private val action: (List<T>) -> Unit) {
    private val mutex = Mutex()
    private var batch: MutableList<T> = ArrayList(size)

    suspend fun flush() {
        if (batch.isEmpty()) return
        val oldBatch = run {
            val newBatch = ArrayList<T>(size)
            mutex.withLock {
                batch.also { batch = newBatch }
            }
        }
        action.invoke(oldBatch)
    }

    suspend fun add(obj: T) {
        mutex.withLock {
            batch.add(obj)
        }
    }
}