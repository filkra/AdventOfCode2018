package de.hhu.krakowski.frequency

import de.hhu.krakowski.frequency.sequences.duplicates

fun main(args: Array<String>) {

    // Read data from standard input
    val input = System.`in`.bufferedReader().use { it.readText() }
            .lines()
            .filter { it.isNotBlank() }
            .map { it.toInt() }

    // Task 1
    val sum = input.reduce { acc, i ->  acc + i }
    println("The resulting frequency is ${sum}")

    // Task 2
    val listSize = input.size
    val frequency = generateSequence(Pair(0,0)) { Pair(it.first + 1, it.second + input[it.first % listSize]) }
            .map { it.second }
            .duplicates()
            .first()

    println("The first frequency reached twice is ${frequency}")
}

