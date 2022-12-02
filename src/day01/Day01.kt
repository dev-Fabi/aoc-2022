package day01

import Utils

fun main() {
    val utils = Utils(1)

    fun String.getCaloriesPerElf() = split("\n\n")
        .map { it.split("\n").map(String::toLong) }
        .map { it.sum() }

    fun part1(input: String): Long {
        return input.getCaloriesPerElf().max()
    }

    fun part2(input: String): Long {
        return input.getCaloriesPerElf().sortedDescending().take(3).sum()
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readFile("test")
    check(part1(testInput) == 24000L)
    check(part2(testInput) == 45000L)

    // Solve puzzle and print result
    val input = utils.readFile()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}