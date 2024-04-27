package org.vivlaniv.nexohub

import kotlinx.serialization.Serializable

@Serializable
enum class Type {
    BOOLEAN,        // 0 or 1
    PERCENT,        // value from 0 to 100
    BYTE,           // value from 0 to 255
    TEMPERATURE     // temperature value
}

@Serializable
data class Schema(
    val type: Type,
    val min: Int? = null,
    val max: Int? = null
)

@Serializable
data class PropertyInfo(
    val name: String,
    val schema: Schema,
    val readOnly: Boolean,
    val value: Int
)

@Serializable
data class SignalInfo(
    val name: String,
    val args: List<Schema>
)

@Serializable
data class DeviceInfo(
    val id: String,
    val type: String,
    val properties: List<PropertyInfo>,
    val signals: List<SignalInfo>
)

@Serializable
data class SavedDeviceInfo(
    val id: String,
    val type: String,
    val user: String,
    val room: String?,
    val alias: String?
)

@Serializable
data class SavedDevice(
    val id: String,
    val type: String,
    val room: String?,
    val alias: String?,
    var properties: List<PropertyInfo>,
    val signals: List<SignalInfo>
)
