package de.hhu.krakowski.fabric

import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {

    // Read data from standard input
    val input = System.`in`.bufferedReader().use { it.readText() }
            .lines()
            .filter { it.isNotBlank() }
            .map { Claim.fromString(it) }

    // Task 1
    val sum = input.flatMap { getPoints(it) }
            .groupingBy { it }
            .eachCount()
            .filter { it.value >= 2 }

    println(sum.count())

    // Task 2
    val claim = input.map { Pair(it, input.minus(it)) }
            .filter { pair-> pair.second.none { pair.first.boundingBox.isOverlapping(it.boundingBox) } }
            .map { it.first.id }
            .first()

    println(claim)
}

fun getPoints(claim: Claim) : List<Pair<Int, Int>> {
    val points = ArrayList<Pair<Int, Int>>()
    for (x in claim.boundingBox.x1 until claim.boundingBox.x2) {
        for (y in claim.boundingBox.y1 until claim.boundingBox.y2) {
            points.add(Pair(x, y))
        }
    }
    return points
}

data class Rectangle (
        val x1: Int,
        val x2: Int,
        val y1: Int,
        val y2: Int
) {

    fun isOverlapping(other: Rectangle) : Boolean {
        return !(other.y2 < y1 || other.y1 > y2 || other.x2 < x1 || other.x1 > x2)
    }
}

data class Claim (
        val id: Int,
        val boundingBox: Rectangle
) {

    companion object {

        val regex = """#(\d+)\W@\W(\d+),(\d+):\W(\d+)x(\d+)""".toRegex()

        val emptyClaim = Claim(0, Rectangle(0, 0, 0, 0))

        fun fromString(claim: String) : Claim {
            val result = regex.find(claim) ?: return emptyClaim

            val (id, offsetX, offsetY, width, height) = result.destructured

            return Claim(id.toInt(), Rectangle(offsetX.toInt(), offsetX.toInt() + width.toInt(),
                                               offsetY.toInt(), offsetY.toInt() + height.toInt()))
        }
    }
}