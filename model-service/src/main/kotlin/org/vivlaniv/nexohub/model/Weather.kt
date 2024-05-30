package org.vivlaniv.nexohub.model

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.vivlaniv.nexohub.model.device.Teapot
import java.util.Random
import kotlin.math.max
import kotlin.math.min

val log: Logger = LoggerFactory.getLogger("model-weather")

fun World.doWeatherAction() {
    houses.forEach { house ->
        val event = Random().nextInt(3)
        when (event) {
            0 -> {
                log.info("Nothing happened with house ${house.id}")
            }

            1 -> {
                val heat = Random().nextInt(7) + 1
                house.rooms.forEach { it.temperature = min(40, it.temperature + heat) }
                val dry = Random().nextInt(5) + 1
                house.rooms.forEach { it.humidity = max(15, it.humidity - dry) }
                log.info("House ${house.id} became $heat degrees warmer and $dry percents drier")
            }

            2 -> {
                val cool = Random().nextInt(7) + 1
                house.rooms.forEach { it.temperature = max(10, it.temperature - cool) }
                val wet = Random().nextInt(5) + 1
                house.rooms.forEach { it.humidity = min(75, it.humidity + wet) }
                log.info("House ${house.id} became $cool degrees colder and $wet percents wetter")
            }
        }
    }
}

fun World.keepHeatableTemperature() {
    houses.forEach { house ->
        house.rooms.forEach { room ->
            room.devices.forEach { device ->
                if (device is Heatable) {
                    if (device.temperature < room.temperature) device.temperature++
                    else if (device.temperature > room.temperature) device.temperature--
                }
            }
        }
    }
}

fun World.doEvaporate() {
    houses.forEach { house ->
        house.rooms.forEach { room ->
            if (room.humidity > 10) room.humidity--
        }
    }
}

fun World.followHeatersTemperature() {
    houses.forEach { house ->
        house.rooms.forEach { room ->
            room.devices.forEach { device ->
                if (device is Heater && device.turn != 0) {
                    if (room.temperature < device.temperature) room.temperature++
                    else if (room.temperature > device.temperature) room.temperature--
                }
            }
        }
    }
}

fun World.followWettorsHumidity() {
    houses.forEach { house ->
        house.rooms.forEach { room ->
            room.devices.forEach { device ->
                if (device is Wettor && device.turn != 0) {
                    if (room.humidity < device.humidity) room.humidity++
                }
            }
        }
    }
}

fun World.putSensorValues() {
    houses.forEach { house ->
        house.rooms.forEach { room ->
            room.devices.forEach { device ->
                if (device is TemperatureSensor) {
                    device.tempSensor = room.temperature
                }
                if (device is HumiditySensor) {
                    device.humidSensor = room.humidity
                }
            }
        }
    }
}

fun World.changeTeapotTemperature() {
    houses.forEach { house ->
        house.rooms.forEach { room ->
            room.devices.forEach { device ->
                if (device is Teapot) {
                    when (val mode = device.mode) {
                        Teapot.TeaMode.BOIL -> {
                            if (device.temperature < mode.temperature) device.temperature++
                            if (device.temperature >= mode.temperature) device.mode =
                                Teapot.TeaMode.NONE
                        }

                        Teapot.TeaMode.HOLD_HOT, Teapot.TeaMode.HOLD_WARM -> {
                            if (device.temperature < mode.temperature) device.temperature++
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}