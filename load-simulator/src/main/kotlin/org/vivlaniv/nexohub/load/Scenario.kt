package org.vivlaniv.nexohub.load

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.lang3.RandomStringUtils
import org.vivlaniv.nexohub.common.DeviceInfo
import org.vivlaniv.nexohub.common.task.*
import org.vivlaniv.nexohub.common.util.mqtt.publish
import org.vivlaniv.nexohub.common.util.mqtt.subscribe
import java.util.concurrent.atomic.AtomicInteger

suspend fun Application.startScenario() = coroutineScope {
    val startupDelay = props.getProperty("load.startup.delay", "5000").toLong()
    log.info("Startup delay $startupDelay ms, waiting")
    delay(startupDelay)

    log.info("Starting scenario")

    val count = props.getProperty("load.users.count", "500").toInt()
    log.info("Generating $count users")

    val start = System.nanoTime()
    val completed = AtomicInteger(0)

    val createDelay = props.getProperty("load.user.delay", "500").toLong()

    repeat(count) {
        launch {
            startUserScenario {
                completed.incrementAndGet().also {
                    log.info("User scenario completed for $it users")
                    if (it == count) {
                        val end = System.nanoTime()
                        log.info("User scenario completed for all users within ${(end - start)/1_000_000}ms")
                    }
                }
            }
        }
        delay(createDelay)
    }

    log.info("Generation finished")
}

private fun Application.startUserScenario(onCompleted: () -> Unit) {
    val user = RandomStringUtils.randomAlphabetic(10)
    val pass = RandomStringUtils.randomAscii(10)

    fun onFailure(action: String): () -> Unit = {
        log.info("user $user $action fail")
        onCompleted()
    }

    val onSuccessAuth: suspend (String) -> Unit = { token ->
        log.info("user $user authentication success")
        delay(3_000)
        searchDevices(token,
            onSuccess = { devices ->
                log.info("user $user devices $devices")
                onCompleted()
            },
            onFailure = onFailure("search devices")
        )
    }

    val onSuccessRegistration: suspend () -> Unit = {
        log.info("user $user registration success")
        delay(3_000)
        authUser(user, pass,
            onSuccess = onSuccessAuth,
            onFailure = onFailure("authentication")
        )
    }

    registerUser(user, pass,
        onSuccess = onSuccessRegistration,
        onFailure = onFailure("registration")
    )
}

fun Application.registerUser(user: String, pass: String, onSuccess: suspend () -> Unit, onFailure: () -> Unit) {
    val task = RegisterUserTask(username = user, password = pass)
    val topic = "${task.id}/signup"
    mqtt.subscribe<RegisterUserTaskResult>("$topic/out") { tpc, result ->
        mqtt.unsubscribe(tpc)
        if (result.code == 0) {
            onSuccess()
        } else {
            onFailure()
        }
    }
    mqtt.publish("$topic/in", task)
}

fun Application.authUser(user: String, pass: String, onSuccess: suspend (String) -> Unit, onFailure: () -> Unit) {
    val task = AuthUserTask(username = user, password = pass)
    val topic = "${task.id}/signin"
    mqtt.subscribe<AuthUserTaskResult>("$topic/out") { tpc, result ->
        mqtt.unsubscribe(tpc)
        if (result.code == 0) {
            onSuccess(result.token!!)
        } else {
            onFailure()
        }
    }
    mqtt.publish("$topic/in", task)
}

fun Application.searchDevices(token: String, onSuccess: (List<DeviceInfo>) -> Unit, onFailure: () -> Unit) {
    val topic = "$token/search"
    mqtt.subscribe<SearchDevicesTaskResult>("$topic/out") { tpc, result ->
        mqtt.unsubscribe(tpc)
        if (result.code == 0) {
            onSuccess(result.devices!!)
        } else {
            onFailure()
        }
    }
    mqtt.publish("$topic/in", SearchDevicesTask())
}