package day06

import Utils

fun main() {
    val utils = Utils(6)

    fun String.endIndexOfFirstNUniqueChars(n: Int) = windowedSequence(n)
        .indexOfFirst { it.toSet().size == n } + n

    fun part1(input: String): Int {
        return input.endIndexOfFirstNUniqueChars(4)
    }

    fun part2(input: String): Int {
        return input.endIndexOfFirstNUniqueChars(14)
    }

    // Test if implementation meets criteria from the description
    check(part1("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 7)
    check(part1("bvwbjplbgvbhsrlpgdmjqwftvncz") == 5)
    check(part1("nppdvjthqldpwncqszvftbrmjlhg") == 6)
    check(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)
    check(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 11)

    check(part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 19)
    check(part2("bvwbjplbgvbhsrlpgdmjqwftvncz") == 23)
    check(part2("nppdvjthqldpwncqszvftbrmjlhg") == 23)
    check(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 29)
    check(part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 26)

    // Solve puzzle and print result
    val input = utils.readFile()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}