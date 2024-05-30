package org.vivlaniv.nexohub.connection.util

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.redisson.api.RedissonClient
import org.vivlaniv.nexohub.common.task.Task
import org.vivlaniv.nexohub.common.task.TaskResult
import org.vivlaniv.nexohub.common.util.redis.publish
import org.vivlaniv.nexohub.common.util.redis.subscribe
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException

val waitResponse = ConcurrentHashMap<String, CompletableDeferred<TaskResult>>()

/**
 * TimeoutException must be catch on Deferred.await()
 */
inline fun <reified T : Task> CoroutineScope.sendAsync(redis: RedissonClient, topic: String, task: T): Deferred<TaskResult> {
    val resultDeferred = CompletableDeferred<TaskResult>()

    launch {
        delay(3_000)
        waitResponse.remove(task.id)?.completeExceptionally(TimeoutException())
    }

    waitResponse[task.id] = resultDeferred
    redis.publish<T>(topic, task)
    return resultDeferred
}

inline fun <reified T : TaskResult> completeAsync(result: T) {
    waitResponse.remove(result.tid)?.complete(result)
}

inline fun <reified T : TaskResult> RedissonClient.requireAsyncCompleter(topic: String) {
    if (getTopic(topic).countListeners() > 0) return
    subscribe<T>(topic) { completeAsync<T>(it) }
}