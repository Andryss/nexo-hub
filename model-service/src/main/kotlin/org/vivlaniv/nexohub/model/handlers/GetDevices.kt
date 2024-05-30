package org.vivlaniv.nexohub.model.handlers

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.faker
import org.vivlaniv.nexohub.model.Application
import org.vivlaniv.nexohub.common.task.GetDevicesTask
import org.vivlaniv.nexohub.common.task.GetDevicesTaskResult
import org.vivlaniv.nexohub.model.House
import org.vivlaniv.nexohub.model.Room
import org.vivlaniv.nexohub.model.device.Device
import org.vivlaniv.nexohub.model.device.Humidifier
import org.vivlaniv.nexohub.model.device.Lamp
import org.vivlaniv.nexohub.model.device.Socket
import org.vivlaniv.nexohub.model.device.THSensor
import org.vivlaniv.nexohub.model.device.Teapot
import org.vivlaniv.nexohub.model.device.Thermostat
import org.vivlaniv.nexohub.common.util.redis.publish
import org.vivlaniv.nexohub.common.util.redis.subscribe
import java.util.Random

fun Application.configureGetDevicesHandler() {
    val getDevicesTopic = props.getProperty("topic.get.devices", "devices/get")

    redis.subscribe<GetDevicesTask>("$getDevicesTopic/in") { task ->
        val devices = userToDevices[task.user] ?: initHouseFor(task.user)
        var infos = devices.values.map { it.getInfo() }
        task.include?.let { include -> infos = infos.filter { include.contains(it.id) } }
        task.exclude?.let { exclude -> infos = infos.filter { !exclude.contains(it.id) } }
        val result = GetDevicesTaskResult(tid = task.id, devices = infos)
        redis.publish("$getDevicesTopic/out", result)
    }

    faker = faker {
        fakerConfig {
            locale = "en"
        }
    }

    log.info("Get devices handler configured")
}

lateinit var faker : Faker

fun Application.initHouseFor(user: String): MutableMap<String, Device> {
    val house = run {
        if (Random().nextInt(3) < 1) { firstConfiguration(user) }
        else if (Random().nextInt(2) < 1) { secondConfiguration(user) }
        else { thirdConfiguration(user) }
    }
    houses.add(house)
    userToDevices[user] = mutableMapOf(
        *house.rooms.flatMap { it.devices }.map { it.getId() to it }.toTypedArray()
    )
    return userToDevices[user]!!
}

fun firstConfiguration(user: String): House {
    return House(
        id = faker.address.fullAddress(),
        owner = user,
        rooms = listOf(
            Room(devices = listOf(Lamp(), Lamp(), Lamp(), Teapot(), Teapot(), Socket()))
        )
    )
}

fun secondConfiguration(user: String): House {
    return House(
        id = faker.address.fullAddress(),
        owner = user,
        rooms = listOf(
            Room(devices = listOf(Thermostat(), Humidifier(), THSensor())),
            Room(devices = listOf(Thermostat(), Humidifier(), THSensor()))
        )
    )
}

fun thirdConfiguration(user: String): House {
    return House(
        id = faker.address.fullAddress(),
        owner = user,
        rooms = listOf(
            Room(devices = listOf(Lamp(), Teapot(), Socket(), THSensor())),
            Room(devices = listOf(Socket(), Thermostat(), THSensor())),
            Room(devices = listOf(Teapot(), Thermostat(), THSensor()))
        )
    )
}