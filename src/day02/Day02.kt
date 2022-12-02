package day02

import Utils

fun main() {
    val utils = Utils(2)

    fun List<String>.getPairs() = this.map {
        val (elf, self) = it.split(" ")
        elf.first() to self.first()
    }

    fun Char.neededToWin(): Char = if (this + 1 > 'C') 'A' else this + 1
    fun Char.neededToLoose(): Char = if (this - 1 < 'A') 'C' else this - 1

    fun Char.getScore(): Int = this - 'A' + 1

    fun part1(input: List<String>): Int {
        fun Pair<Char, Char>.map() = this.first to this.second - ('X' - 'A')
        fun Pair<Char, Char>.roundScore(): Int = when (second) {
            first.neededToWin() -> 6 // Win
            first -> 3 // Draw
            else -> 0 // Lost
        }

        val result = input.getPairs()
            .map { it.map() }
            .sumOf {
                it.roundScore() + it.second.getScore()
            }
        return result
    }

    fun part2(input: List<String>): Int {
        fun Char.roundScore(): Int = (this - 'X') * 3

        fun Pair<Char, Char>.mustSelect(): Char = when (this.second) {
            'X' -> this.first.neededToLoose()
            'Y' -> this.first
            'Z' -> this.first.neededToWin()
            else -> error("Invalid")
        }

        return input.getPairs()
            .sumOf {
                it.second.roundScore() + it.mustSelect().getScore()
            }
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}