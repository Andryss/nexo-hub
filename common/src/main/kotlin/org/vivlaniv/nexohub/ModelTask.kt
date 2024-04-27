package org.vivlaniv.nexohub

import kotlinx.serialization.Serializable
import java.util.*

// model-service tasks

@Serializable
data class GetDevicesTask(
    override val id: String = UUID.randomUUID().toString(),
    val user: String,
    val include: List<String>? = null,
    val exclude: List<String>? = null
) : Task

@Serializable
data class GetDevicesTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null,
    val devices: List<DeviceInfo>? = null
) : TaskResult


@Serializable
data class GetDeviceTask(
    override val id: String = UUID.randomUUID().toString(),
    val user: String,
    val device: String
) : Task

@Serializable
data class GetDeviceTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null,
    val device: DeviceInfo? = null
) : TaskResult


@Serializable
data class GetDevicesPropertiesTask(
    override val id: String = UUID.randomUUID().toString(),
    val user: String,
    val include: List<String>? = null
) : Task

@Serializable
data class GetDevicesPropertiesTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null,
    val properties: Map<String, List<PropertyInfo>>? = null
) : TaskResult


@Serializable
data class SetDevicePropertyTask(
    override val id: String = UUID.randomUUID().toString(),
    val user: String,
    val device: String,
    val name: String,
    val value: Int
) : Task

@Serializable
data class SetDevicePropertyTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null
) : TaskResult


@Serializable
data class SignalDeviceTask(
    override val id: String = UUID.randomUUID().toString(),
    val user: String,
    val device: String,
    val name: String,
    val args: List<Int>
) : Task

@Serializable
data class SignalDeviceTaskResult(
    override val tid: String,
    override val code: Int = 0,
    override val errorMessage: String? = null
) : TaskResult