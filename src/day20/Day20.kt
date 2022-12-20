package day20

import Utils

fun main() {
    val utils = Utils(20)

    class Element(val number: Long)

    fun List<Element>.mix(times: Int): List<Element> {
        val mixedList = this.toMutableList()

        repeat(times) {
            for (nextElement in this) {
                val currentIndex = mixedList.indexOf(nextElement)
                val current = mixedList.removeAt(currentIndex)

                var newIndex = ((currentIndex + current.number) % mixedList.size).toInt()
                if (newIndex <= 0) {
                    newIndex += mixedList.size
                }

                mixedList.add(newIndex, current)
            }
        }

        return mixedList
    }

    fun List<Element>.coordinateSum(): Long {
        val zeroIndex = this.indexOfFirst { it.number == 0L }

        return listOf(1000, 2000, 3000).sumOf {
            this[(zeroIndex + it) % this.size].number
        }
    }

    fun part1(input: List<String>): Long {
        val encryptedNumbers = input.map { Element(it.toLong()) }
        val mixedNumbers = encryptedNumbers.mix(1)

        return mixedNumbers.coordinateSum()
    }

    fun part2(input: List<String>): Long {
        val encryptedNumbers = input.map { Element(it.toLong() * 811589153) }
        val mixedNumbers = encryptedNumbers.mix(10)

        return mixedNumbers.coordinateSum()
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}