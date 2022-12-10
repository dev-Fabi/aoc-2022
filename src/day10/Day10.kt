package day10

import Utils

fun main() {
    val utils = Utils(10)

    fun List<String>.execute(processCycle: (cycle: Int, registerX: Int) -> Unit) {
        var registerX = 1
        var cycle = 1

        for (instruction in this) {
            when {
                instruction == "noop" -> {
                    processCycle(cycle, registerX)
                    cycle++
                }

                instruction.startsWith("addx ") -> {
                    repeat(2) {
                        processCycle(cycle, registerX)
                        cycle++
                    }

                    registerX += instruction.substringAfter(" ").toInt()
                }

                else -> error("Unexpected instruction")
            }
        }
    }

    fun part1(input: List<String>): Long {
        var signalStrength = 0L

        input.execute { cycle, registerX ->
            if (cycle % 40 == 20) {
                signalStrength += cycle * registerX
            }
        }

        return signalStrength
    }

    fun part2(input: List<String>): String {
        val image = Array(6) { Array(40) { '.' } }

        input.execute { cycle, registerX ->
            val x = (cycle - 1) % 40
            val drawingRange = (registerX - 1)..(registerX + 1)
            if (x in drawingRange) {
                val row = (cycle - 1) / 40
                image[row][x] = '#'
            }
        }

        return image.joinToString("\n") { it.joinToString("") }
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    /*val testInput = """noop,addx 3,addx -5""".split(",")*/
    check(part1(testInput) == 13140L)
    println(part2(testInput))

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: \n" + part2(input).prependIndent("\t\t"))
}