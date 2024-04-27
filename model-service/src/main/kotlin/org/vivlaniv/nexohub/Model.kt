package org.vivlaniv.nexohub

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.math.max
import kotlin.math.min

interface Device {
    fun getInfo(): DeviceInfo = DeviceInfo(getId(), getType(), getProperties(), getSignals())
    fun getId(): String
    fun getType(): String = javaClass.simpleName
    fun getProperties(): List<PropertyInfo>
    fun setProperty(name: String, value: Int)
    fun getSignals(): List<SignalInfo>
    fun signal(name: String, args: List<Int>)
}

abstract class AbstractDevice : Device {
    private val id = UUID.randomUUID().toString()
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun getId(): String = id
    override fun getProperties() = listOf<PropertyInfo>()
    override fun setProperty(name: String, value: Int) = log.warn("unknown property $name for $this")
    override fun getSignals() = listOf<SignalInfo>()
    override fun signal(name: String, args: List<Int>) = log.warn("unknown signal $name for $this")
}

class Lamp(
    private var turn: Int = 0,
    private var brightness: Int = 80,
    private var red: Int = 255,
    private var green: Int = 255,
    private var blue: Int = 255
) : AbstractDevice() {
    override fun getProperties() = listOf(
        PropertyInfo("turn", Schema(Type.BOOLEAN), false, turn),
        PropertyInfo("brightness", Schema(Type.PERCENT), false, brightness),
        PropertyInfo("red", Schema(Type.BYTE), false, red),
        PropertyInfo("green", Schema(Type.BYTE), false, green),
        PropertyInfo("blue", Schema(Type.BYTE), false, blue)
    )

    override fun setProperty(name: String, value: Int) {
        when (name) {
            "turn" -> turn = max(0, min(1, value))
            "brightness" -> brightness = max(0, min(100, value))
            "red" -> red = max(0, min(255, value))
            "green" -> green = max(0, min(255, value))
            "blue" -> blue = max(0, min(255, value))
            else -> super.setProperty(name, value)
        }
    }

    override fun toString(): String {
        return "Lamp(turn=$turn, brightness=$brightness, red=$red, green=$green, blue=$blue)"
    }
}

class Teapot(
    private var volume: Int = 70,
    private var temperature: Int = 20
) : AbstractDevice() {
    init {
        // TODO: coroutines
        Thread {
            while (true) {
                if (temperature > 20) temperature--
                Thread.sleep(10_000)
            }
        }.start()
    }

    override fun getProperties() = listOf(
        PropertyInfo("volume", Schema(Type.PERCENT), true, volume),
        PropertyInfo("temperature", Schema(Type.TEMPERATURE), true, temperature)
    )

    override fun getSignals() = listOf(
        SignalInfo("boil", listOf()),
        SignalInfo("hold_temperature", listOf(Schema(Type.TEMPERATURE, min = 20, max = 100)))
    )

    override fun signal(name: String, args: List<Int>) {
        // TODO: coroutines
        when (name) {
            "boil" -> Thread {
                while (temperature < 100) {
                    temperature++
                    Thread.sleep(1_000)
                }
            }.start()
            "hold_temperature" -> Thread {
                val target = max(20, min(100, args[0]))
                while (temperature < target) {
                    temperature++
                    Thread.sleep(1_000)
                }
                val finish = System.currentTimeMillis() + 60_000
                while (System.currentTimeMillis() < finish) {
                    if (temperature < target) temperature++
                    Thread.sleep(1_000)
                }
            }.start()
            else -> super.signal(name, args)
        }
    }

    override fun toString(): String {
        return "Teapot(volume=$volume, temperature=$temperature)"
    }
}