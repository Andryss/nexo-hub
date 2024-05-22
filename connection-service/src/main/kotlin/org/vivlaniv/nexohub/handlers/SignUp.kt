package org.vivlaniv.nexohub.handlers

import org.mindrot.jbcrypt.BCrypt
import org.vivlaniv.nexohub.Application
import org.vivlaniv.nexohub.RegisterUserTask
import org.vivlaniv.nexohub.RegisterUserTaskResult
import org.vivlaniv.nexohub.SaveUserTask
import org.vivlaniv.nexohub.SaveUserTaskResult
import org.vivlaniv.nexohub.util.async.requireAsyncCompleter
import org.vivlaniv.nexohub.util.async.sendAsync
import org.vivlaniv.nexohub.util.mqtt.publish
import org.vivlaniv.nexohub.util.mqtt.subscribe
import java.util.concurrent.TimeoutException

fun Application.configureSignUpHandler() {
    val saveUserTopic = props.getProperty("topic.save.user", "users/save")

    redis.requireAsyncCompleter<SaveUserTaskResult>("$saveUserTopic/out")

    mqtt.subscribe<RegisterUserTask>("+/signup/in") { topic, request ->
        val transaction = topic.substringBefore('/')

        val response = try {
            val saveUserTaskResult = sendAsync(redis,
                "$saveUserTopic/in",
                SaveUserTask(
                    username = request.username,
                    passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())
                )
            ).await() as SaveUserTaskResult

            if (saveUserTaskResult.code != 0) {
                RegisterUserTaskResult(request.id, 1, "user ${request.username} already exist")
            } else {
                RegisterUserTaskResult(request.id)
            }
        } catch (e: TimeoutException) {
            RegisterUserTaskResult(request.id, 1, "timeout occurred, try again later")
        }

        mqtt.publish("$transaction/signup/out", response)
    }

    log.info("Sign up handler configured")
}