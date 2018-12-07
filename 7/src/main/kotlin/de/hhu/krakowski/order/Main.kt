package de.hhu.krakowski.grid

import java.lang.StringBuilder
import java.util.*

val regex = """Step\W(\w)\Wmust\Wbe\Wfinished\Wbefore\Wstep\W(\w)\Wcan\Wbegin.""".toRegex()

fun main(args: Array<String>) {

    // Read data from standard input
    val input = System.`in`.bufferedReader().use { it.readText() }
            .lines()
            .filter { it.isNotBlank() }
            .mapNotNull { regex.matchEntire(it) }
            .map { Pair(it.destructured.component2(), it.destructured.component1()) }

    // Task 1
    val nodes = input.flatMap { listOf(it.first, it.second) }
            .distinct()

    var scheduler = Scheduler(nodes)

    input.forEach { scheduler.addDependency(it) }

    var builder = StringBuilder()

    nodes.forEach {
        val nextStep = scheduler.getNextStep()
        builder.append(nextStep)
        scheduler.removeStep(nextStep)
    }

    println(builder.toString())

    // Task 2
    val worker = (1..5).map { Worker() }

    scheduler = Scheduler(nodes)

    input.forEach { scheduler.addDependency(it) }

    builder = StringBuilder()

    while (builder.length != nodes.size) {
        worker.filter { it.canWork() }
    }

    println(builder.toString())
}

class Scheduler(nodes: List<String>) {

    var steps = HashMap<String, MutableList<String>>()

    init {
        nodes.forEach { steps.put(it, mutableListOf()) }
    }

    fun addDependency(dependency: Pair<String, String>) {
        val list = steps[dependency.first] ?: mutableListOf()
        list.add(dependency.second)
        steps[dependency.first] = list
    }

    fun getNextStep(): String {
        val nextStep = steps
                .filterValues { it.isEmpty() }
                .map { it.key }
                .minBy { it } ?: return ""

        return nextStep
    }

    fun removeStep(step: String) {
        steps.forEach { it.value.remove(step) }
        steps.remove(step)
    }
}

class Worker() {

    var work = ""
    var totalTime = 0
    var workTime = 0

    fun setWork(work: String, workTime: Int) {
        this.work = work
        this.workTime = workTime
    }

    fun tick() {
        workTime--
        totalTime++
    }

    fun canWork() = workTime == 0
}

