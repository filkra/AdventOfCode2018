package de.hhu.krakowski.guard

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.IntStream
import kotlin.streams.toList

fun main(args: Array<String>) {

    // Read data from standard input
    val input = System.`in`.bufferedReader().use { it.readText() }
            .lines()
            .filter { it.isNotBlank() }

    // Task 1
    val events = input.map { parseEvent(it) }
            .filter { it.type != EventType.INVALID }
            .sortedBy { it.time }

    val guards = events.filter { it.type  == EventType.SHIFT_BEGAN }
            .map { (it as ShiftBeganEvent).guardId }
            .distinct()
            .associate { Pair(it, mutableListOf<Duration>()) }

    var currentGuard = 0
    var startMinute = 0
    for (event in events) {
        when(event.type) {
            EventType.SHIFT_BEGAN -> currentGuard = (event as ShiftBeganEvent).guardId
            EventType.FELL_ASLEEP -> startMinute = event.time.minute
            EventType.WOKE_UP -> guards[currentGuard]?.add(Duration(startMinute, event.time.minute))
            EventType.INVALID -> {}
        }
    }

    val firstGuard = guards.mapValues { it.value.fold(0) { acc, duration ->  acc + duration.getDifference()} }
            .maxBy { it -> it.value } ?: return

    val minute = guards[firstGuard.key]
            ?.flatMap { IntStream.range(it.start, it.end).toList() }
            ?.groupingBy { it }
            ?.eachCount()
            ?.maxBy { it.value }

    println("Guard #${firstGuard.key} slept the most during minute ${minute?.key}.")

    // Task 2
    val secondGuard = guards
            .mapValues { it.value.flatMap { IntStream.range(it.start, it.end).toList() } }
            .mapValues { it.value.groupingBy { x -> x }.eachCount().maxBy { m -> m.value } }
            .maxBy { it.value?.value ?: 0 } ?: return

    println("Guard #${secondGuard.key} is most frequently asleep during minute ${secondGuard.value?.key}")
}

val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
val shiftBeganRegex = """\[(\d+-\d+-\d+\W\d+:\d+)\]\WGuard\W#(\d+)\Wbegins\Wshift""".toRegex()
val fellAsleepRegex = """\[(\d+-\d+-\d+\W\d+:\d+)\]\Wfalls\Wasleep""".toRegex()
val wokeUpRegex = """\[(\d+-\d+-\d+\W\d+:\d+)\]\Wwakes\Wup""".toRegex()

fun parseDateTime(time: String) : LocalDateTime {
    return LocalDateTime.parse(time, dateFormatter)
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

enum class EventType { SHIFT_BEGAN, FELL_ASLEEP, WOKE_UP, INVALID }
open class TimedEvent (val type: EventType, val time: LocalDateTime)
class ShiftBeganEvent(val guardId: Int, time: LocalDateTime) : TimedEvent(EventType.SHIFT_BEGAN, time)
class FellAsleepEvent(time: LocalDateTime) : TimedEvent(EventType.FELL_ASLEEP, time)
class WokeUpEvent(time: LocalDateTime) : TimedEvent(EventType.WOKE_UP, time)
class InvalidEvent(time: LocalDateTime) : TimedEvent(EventType.INVALID, time)

class Duration (val start: Int, val end: Int) {
    fun getDifference() = end - start
    override fun toString(): String {
        return "(${start} - ${end})"
    }
}

