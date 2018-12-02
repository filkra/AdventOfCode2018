package de.hhu.krakowski.checksum

fun main(args: Array<String>) {

    // Read data from standard input
    val input = System.`in`.bufferedReader().use { it.readText() }
            .lines()
            .filter { it.isNotBlank() }

    // Task 1
    val checksum = input.map { countUniqueCharacters(it) }
            .map { Pair(if (it.containsValue(2)) 1 else 0, if (it.containsValue(3)) 1 else 0) }
            .reduce { acc , pair -> Pair(acc.first + pair.first, acc.second + pair.second) }
            .let { it.first * it.second }

    println("The checksum is ${checksum}")

    // Task 2
    val similar = input.map { hammingFilter(it, input, 1) }
            .filter { it.isNotEmpty() }
            .map { it.first() }
            .reduce {acc, s -> sameCharacters(acc, s)}

    println("The common letters are ${similar}")
}

fun sameCharacters(first: String, second: String) : String {
    return first.zip(second)
            .filter { it.first == it.second }
            .joinToString("") { it.first.toString() }
}

fun countUniqueCharacters(word: String) : Map<Char, Int> {
    val map = HashMap<Char, Int>()
    word.forEach { map[it] = (map[it] ?: 0) + 1 }
    return map
}

fun hammingFilter(word: String, compareList: List<String>, distance: Int): List<String> {
    return compareList.filter { hammingDistance(word, it) == distance }
}

fun hammingDistance(first: String, second: String) : Int {
    return first.toCharArray()
            .zip(second.toCharArray())
            .map { if (it.first == it.second) 0 else 1 }
            .reduce { acc, i ->  acc + i}
}