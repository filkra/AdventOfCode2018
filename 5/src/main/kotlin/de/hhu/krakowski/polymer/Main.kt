package de.hhu.krakowski.polymer

import java.awt.Event
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.streams.toList

fun main(args: Array<String>) {

    // Read data from standard input
    val input = System.`in`.bufferedReader().use { it.readText() }.trim()

    // Task 1
    var then = System.nanoTime()
    println("${react(input).length} units remain after fully reacting the polymer")
    println(System.nanoTime() - then)

    // Task 2
    then = System.nanoTime()
    val length = Stream.iterate('A') { it + 1 }
            .limit('Z' - 'A')
            .parallel()
            .map { input.replace(it.toString(), "", true) }
            .map { react(it).length }
            .toList()
            .minBy { it } ?: -1
    println(System.nanoTime() - then)

    println("the length of the shortest polymer is ${length}")
}

fun react(polymer: String) : String {
    var i = 0
    var result = polymer
    while (i < result.length - 1) {
        if (abs(result[i] - result[i + 1]) == 32) {
            result = result.removeRange(i, i + 2)
            if (i != 0) i--
        } else {
            i++
        }
    }

    return result
}

