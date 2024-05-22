package org.vivlaniv.nexohub.handlers

import org.mindrot.jbcrypt.BCrypt
import org.vivlaniv.nexohub.Application
import org.vivlaniv.nexohub.AuthUserTask
import org.vivlaniv.nexohub.AuthUserTaskResult
import org.vivlaniv.nexohub.FindUserTask
import org.vivlaniv.nexohub.FindUserTaskResult
import org.vivlaniv.nexohub.util.async.requireAsyncCompleter
import org.vivlaniv.nexohub.util.async.sendAsync
import org.vivlaniv.nexohub.util.mqtt.publish
import org.vivlaniv.nexohub.util.mqtt.subscribe
import java.util.concurrent.TimeoutException

fun Application.configureSignInHandler() {
    val findUserTopic = props.getProperty("topic.find.user", "users/find")

    redis.requireAsyncCompleter<FindUserTaskResult>("$findUserTopic/out")

    mqtt.subscribe<AuthUserTask>("+/signin/in") { topic, request ->
        val transaction = topic.substringBefore('/')

        val response = try {
            val findUserTaskResult = sendAsync(redis,
                "$findUserTopic/in",
                FindUserTask(username = request.username)
            ).await() as FindUserTaskResult

            if (findUserTaskResult.code != 0) {
                AuthUserTaskResult(request.id, 1, "wrong username or password")
            } else if (!BCrypt.checkpw(request.password, findUserTaskResult.passwordHash)) {
                AuthUserTaskResult(request.id, 1, "wrong username or password")
            } else {
                AuthUserTaskResult(request.id, token = request.username)
            }
        } catch (e: TimeoutException) {
            AuthUserTaskResult(request.id, 1, "timeout occurred, try again later")
        }

        mqtt.publish("$transaction/signin/out", response)
    }

    log.info("Sign in handler configured")
}