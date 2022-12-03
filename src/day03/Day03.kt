package day03

import Utils

fun main() {
    val utils = Utils(3)

    fun List<Char>.prioritySum() = sumOf { if (it.isLowerCase()) it - 'a' + 1 else it - 'A' + 27 }

    fun part1(input: List<String>): Int {
        return input.map { it.take(it.count() / 2) to it.takeLast(it.count() / 2) }
            .flatMap { (a, b) ->
                a.filter { it in b }.toSet()
            }.prioritySum()
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3)
            .map { (a, b, c) ->
                a.toSet().single { it in b && it in c }
            }.prioritySum()
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}