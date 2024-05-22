package org.vivlaniv.nexohub.handlers

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.vivlaniv.nexohub.Application
import org.vivlaniv.nexohub.FindUserTask
import org.vivlaniv.nexohub.FindUserTaskResult
import org.vivlaniv.nexohub.Users
import org.vivlaniv.nexohub.util.redis.publish
import org.vivlaniv.nexohub.util.redis.subscribe

fun Application.configureFindUserHandler() {
    val findUserTopic = props.getProperty("topic.find.user", "users/find")

    redis.subscribe<FindUserTask>("$findUserTopic/in") { task ->
        val userPass = transaction {
            Users.selectAll()
                .where { Users.username eq task.username }
                .map { it[Users.password] }
        }
        val result = if (userPass.isEmpty()) {
            FindUserTaskResult(task.id, 1, "user ${task.username} not found")
        } else {
            FindUserTaskResult(task.id, passwordHash = userPass[0])
        }
        redis.publish("$findUserTopic/out", result)
    }

    log.info("Find user handler configured")
}