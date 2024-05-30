package org.vivlaniv.nexohub.common

import kotlinx.serialization.Serializable

@Serializable
enum class Type {
    BOOLEAN,        // 0 or 1
    PERCENT,        // value from 0 to 100
    COLOR,          // color value (empty byte + red + green + blue)
    TEMPERATURE,    // temperature value
    HUMIDITY,       // humidity value (percent like in range [10,80])
    ENUM,           // ordinal of enum value
}

@Serializable
data class Schema(
    val type: Type,
    val min: Int? = null,
    val max: Int? = null,
    val enumValues: List<String>? = null,
    val isSensor: Boolean? = null,
)

@Serializable
data class PropertyInfo(
    val name: String,
    val schema: Schema,
    val readOnly: Boolean,
    val value: Int
)

@Serializable
data class DeviceInfo(
    val id: String,
    val type: String,
    val properties: List<PropertyInfo>,
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
)
