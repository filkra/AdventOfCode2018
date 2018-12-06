package de.hhu.krakowski.guard

import java.awt.Event
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

val shiftBeganRegex = """\[(\d+-\d+-\d+\W\d+:\d+)\]\WGuard\W#(\d+)\Wbegins\Wshift""".toRegex()
val fellAsleepRegex = """\[(\d+-\d+-\d+\W\d+:\d+)\]\Wfalls\Wasleep""".toRegex()
val wokeUpRegex = """\[(\d+-\d+-\d+\W\d+:\d+)\]\Wwakes\Wup""".toRegex()

class State {

    val guards = HashMap<Int, Int>()

    var currentGuard : Int = 0
    var fellAsleepAt : LocalDateTime = LocalDateTime.MAX

    fun process(event: TimedEvent) {
        when(event.type) {
            EventType.SHIFT_BEGAN -> process(event as ShiftBeganEvent)
            EventType.FELL_ASLEEP -> process(event as FellAsleepEvent)
            EventType.WOKE_UP -> process(event as WokeUpEvent)
            EventType.INVALID -> {}
        }
    }

    private fun process(event: ShiftBeganEvent) {
        currentGuard = event.guardId
    }

    private fun process(event: FellAsleepEvent) {
        fellAsleepAt()
    }

    private fun process(event: WokeUpEvent) {

    }
}

fun main(args: Array<String>) {

    // Read data from standard input
    val input = System.`in`.bufferedReader().use { it.readText() }
            .lines()
            .filter { it.isNotBlank() }

    val events = input.map { parseEvent(it) }
            .filter { it.type != EventType.INVALID }
            .sortedBy { it.time }
            .joinToString("\n")

    println(events)
}

fun parseEvent(event: String) : TimedEvent {
    when  {
        event matches shiftBeganRegex -> {
            val (time, guardId) = shiftBeganRegex.find(event)!!.destructured
            return ShiftBeganEvent(guardId.toInt(), parseDateTime(time))
        }
        event matches fellAsleepRegex -> {
            val (time) = fellAsleepRegex.find(event)!!.destructured
            return FellAsleepEvent(parseDateTime(time))
        }
        event matches wokeUpRegex -> {
            val (time) = wokeUpRegex.find(event)!!.destructured
            return WokeUpEvent(parseDateTime(time))
        }
    }

    return InvalidEvent(LocalDateTime.MIN)
}

enum class EventType {
    SHIFT_BEGAN, FELL_ASLEEP, WOKE_UP, INVALID
}

fun parseDateTime(time: String) : LocalDateTime {
    return LocalDateTime.parse(time, dateFormatter)
}

open class TimedEvent (
        val type: EventType,
        val time: LocalDateTime
)

class ShiftBeganEvent(val guardId: Int, time: LocalDateTime) : TimedEvent(EventType.SHIFT_BEGAN, time) {
    override fun toString(): String {
        return "[${time}][SHIFT_CHANGE]{${guardId}}"
    }
}
class FellAsleepEvent(time: LocalDateTime) : TimedEvent(EventType.FELL_ASLEEP, time) {
    override fun toString(): String {
        return "[${time}][FALLS_ASLEEP]"
    }
}
class WokeUpEvent(time: LocalDateTime) : TimedEvent(EventType.WOKE_UP, time) {
    override fun toString(): String {
        return "[${time}][WAKES_UP]"
    }
}
class InvalidEvent(time: LocalDateTime) : TimedEvent(EventType.INVALID, time)

