package org.vivlaniv.nexohub.common.task

import kotlinx.serialization.Serializable
import org.vivlaniv.nexohub.common.PropertyInfo
import org.vivlaniv.nexohub.common.SavedDeviceInfo
import java.util.*

// db-service tasks

// users/find

@Serializable
data class FindUserTask(
    override val id: String = UUID.randomUUID().toString(),
    val username: String
) : Task

@Serializable
data class FindUserTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null,
    val passwordHash: String? = null
) : TaskResult


// users/save

@Serializable
data class SaveUserTask(
    override val id: String = UUID.randomUUID().toString(),
    val username: String,
    val passwordHash: String
) : Task

@Serializable
data class SaveUserTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null
) : TaskResult


// devices/saved/get

@Serializable
data class GetSavedDevicesTask(
    override val id: String = UUID.randomUUID().toString(),
    val user: String
) : Task

@Serializable
data class GetSavedDevicesTaskResult(
    override val tid: String,
    val devices: List<SavedDeviceInfo>
) : TaskResult


// devices/save

@Serializable
data class SaveUserDeviceTask(
    override val id: String = UUID.randomUUID().toString(),
    val type: String,
    val user: String,
    val device: String,
    val room: String?,
    val alias: String?
) : Task

@Serializable
data class SaveUserDeviceTaskResult(
    override val tid: String
) : TaskResult


// sensors/save

@Serializable
data class SaveSensorsTask(
    override val id: String = UUID.randomUUID().toString(),
    val sensors: Map<String, List<PropertyInfo>>
) : Task