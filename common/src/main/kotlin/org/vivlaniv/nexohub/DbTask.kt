package org.vivlaniv.nexohub

import kotlinx.serialization.Serializable
import java.util.*

// db-service tasks


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


@Serializable
data class SaveSensorsTask(
    override val id: String = UUID.randomUUID().toString(),
    val sensors: Map<String, List<PropertyInfo>>
) : Task