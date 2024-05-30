package org.vivlaniv.nexohub.model

import org.vivlaniv.nexohub.model.device.Device

/**
 * Something that follows room's temperature
 */
interface Heatable {
    var temperature: Int
}

/**
 * Something that room's temperature follows
 */
interface Heater {
    val turn: Int
    var temperature: Int
}

/**
 * Something that room's humidity follows
 */
interface Wettor {
    val turn: Int
    var humidity: Int
}

/**
 * Room temperature sensor
 */
interface TemperatureSensor {
    var tempSensor: Int
}

/**
 * Humidity temperature sensor
 */
interface HumiditySensor {
    var humidSensor: Int
}

/**
 * Class containing all users houses
 */
data class World(
    val houses: List<House>
)

/**
 * Class describing house
 */
data class House(
    val id: String,
    val owner: String,
    val rooms: List<Room>
)

/**
 * Class describing one room
 */
data class Room (
    var temperature: Int = 25,
    var humidity: Int = 40,
    val devices: List<Device>
)