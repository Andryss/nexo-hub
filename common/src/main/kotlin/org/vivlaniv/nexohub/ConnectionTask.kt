package org.vivlaniv.nexohub

import kotlinx.serialization.Serializable
import java.util.*

// connection-service tasks

@Serializable
data class AuthUserTask(
    override val id: String = UUID.randomUUID().toString(),
    val username: String,
    val password: String
) : Task

@Serializable
data class AuthUserTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null,
    val token: String? = null
) : TaskResult


@Serializable
data class RegisterUserTask(
    override val id: String = UUID.randomUUID().toString(),
    val username: String,
    val password: String
) : Task

@Serializable
data class RegisterUserTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null
) : TaskResult


@Serializable
data class SearchDevicesTask(
    override val id: String = UUID.randomUUID().toString()
) : Task

@Serializable
data class SearchDevicesTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null,
    val devices: List<DeviceInfo>? = null
) : TaskResult


@Serializable
data class SaveDeviceTask(
    override val id: String = UUID.randomUUID().toString(),
    val device: String,
    val room: String? = null,
    val alias: String? = null
) : Task

@Serializable
data class SaveDeviceTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null
) : TaskResult


@Serializable
data class FetchSavedDevicesTask(
    override val id: String = UUID.randomUUID().toString()
) : Task

@Serializable
data class FetchSavedDevicesTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null,
    val devices: List<SavedDevice>? = null
) : TaskResult


@Serializable
data class FetchDevicesPropertiesTask(
    override val id: String = UUID.randomUUID().toString(),
    val include: List<String>? = null,
) : Task

@Serializable
data class FetchDevicesPropertiesTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null,
    val properties: Map<String, List<PropertyInfo>>? = null
) : TaskResult


@Serializable
data class PutDevicePropertyTask(
    override val id: String = UUID.randomUUID().toString(),
    val device: String,
    val property: String,
    val value: Int
) : Task

@Serializable
data class PutDevicePropertyTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null,
    val device: String,
) : TaskResult