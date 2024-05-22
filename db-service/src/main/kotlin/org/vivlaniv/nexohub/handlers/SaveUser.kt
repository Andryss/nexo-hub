package org.vivlaniv.nexohub.handlers

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.vivlaniv.nexohub.Application
import org.vivlaniv.nexohub.FindUserTaskResult
import org.vivlaniv.nexohub.SaveUserTask
import org.vivlaniv.nexohub.Users
import org.vivlaniv.nexohub.util.redis.publish
import org.vivlaniv.nexohub.util.redis.subscribe
import java.sql.SQLException

fun Application.configureSaveUserHandler() {
    val saveUserTopic = props.getProperty("topic.save.user", "users/save")

    redis.subscribe<SaveUserTask>("$saveUserTopic/in") { task ->
        val saved = try {
            transaction {
                Users.insert {
                    it[username] = task.username
                    it[password] = task.passwordHash
                }
            }
            true
        } catch (e: SQLException) {
            false
        }
        val result = if (!saved) {
            FindUserTaskResult(task.id, 1, "user ${task.username} already exist")
        } else {
            FindUserTaskResult(task.id)
        }
        redis.publish("$saveUserTopic/out", result)
    }

    log.info("Save user handler configured")
}