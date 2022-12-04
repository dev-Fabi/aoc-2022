package day04

import Utils
import kotlin.math.min

fun main() {
    val utils = Utils(4)

    fun List<String>.toRangePairs() = map { line ->
        val (first, second) = line.split(",").map {
            val (from, to) = it.split("-").map(String::toInt)
            from..to
        }
        first to second
    }

    fun part1(input: List<String>): Int {
        return input.toRangePairs().count { (first, second) ->
            first.intersect(second).count() == min(first.count(), second.count())
        }
    }

    fun part2(input: List<String>): Int {
        return input.toRangePairs().count { (first, second) ->
            first.intersect(second).isNotEmpty()
        }
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}