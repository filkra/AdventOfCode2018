package de.hhu.krakowski.grid

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {

    // Read data from standard input
    val input = System.`in`.bufferedReader().use { it.readText() }
            .lines()
            .filter { it.isNotBlank() }
            .map { it.split(", ") }
            .map { Pair(it[0].toInt(), it[1].toInt())}

    // Task 1
    val gridCorners = input.fold(Rectangle(Point(Int.MAX_VALUE, Int.MAX_VALUE), Point(Int.MIN_VALUE, Int.MIN_VALUE)))
            {acc, pair -> Rectangle(
                    Point(min(acc.topLeft.x, pair.first), min(acc.topLeft.y, pair.second)),
                    Point(max(acc.bottomRight.x, pair.first), max(acc.bottomRight.y, pair.second)))}

    val locations = input.mapIndexed { index, pair ->
        Location(index + 1, Point(pair.first - gridCorners.topLeft.x, pair.second - gridCorners.topLeft.y)) }

    val grid = Array(gridCorners.bottomRight.x - gridCorners.topLeft.x)
            {IntArray(gridCorners.bottomRight.y - gridCorners.topLeft.y)}

    val borderIds = mutableSetOf<Int>()
    for (x in 0 until grid.size) {
        for (y in 0 until grid[0].size) {
            grid[x][y] = findNearest(x, y, locations)

            if (x == 0 || x == grid.size - 1 || y == 0 || y == grid[0].size - 1) {
                borderIds.add(grid[x][y])
            }
        }
    }

    val areas = grid.flatMap { it.toList() }
            .groupingBy { it }
            .eachCount()
            .filter { !borderIds.contains(it.key) }
            .maxBy { it.value }

    println("the size of the largest area that isn't infinite is ${areas?.value}")


    // Task 2
    for (x in 0 until grid.size) {
        for (y in 0 until grid[0].size) {
            grid[x][y] = manhattanSum(x, y, locations)
        }
    }

    val size = grid.flatMap { it.toList() }
            .filter { it < 10000 }
            .count()

    println("the size of the region containing all locations which have a total distance to all given coordinates of less than 10000 is ${size}")
}

fun manhattanSum(x: Int, y: Int, locations: List<Location>) : Int {
    val origin = Point(x, y)
    return locations.map { manhattanDistance(origin, it.point) }
            .sum()
}

fun findNearest(x: Int, y: Int, locations: List<Location>) : Int {
    val origin = Point(x, y)

    val distances = locations.map { Pair(it, manhattanDistance(origin, it.point)) }
            .groupBy { it.second }
            .minBy { it.key }?.value ?: return -1

    return if (distances.size > 1) 0 else distances[0].first.id
}

fun manhattanDistance(first: Point, second: Point) : Int {
    return abs(first.x - second.x) + abs(first.y - second.y)
}

data class Rectangle (val topLeft: Point, val bottomRight: Point)

data class Point(val x: Int, val y: Int)

data class Location (val id: Int, val point: Point)

