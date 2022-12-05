package day05

import Utils

fun main() {
    val utils = Utils(5)

    class Instruction(val amount: Int, val from: Int, val to: Int)

    fun List<List<Char>>.joinTopElementsToString() = joinToString("") { it.last().toString() }
    fun MutableList<*>.removeLast(n: Int) = repeat(n) { this.removeLast() }

    fun parse(input: String): Pair<List<MutableList<Char>>, List<Instruction>> {
        val (rawStacks, rawInstructions) = input.split("\n\n")
        val stackLines = rawStacks.split("\n").reversed()

        val stacks = (0 until stackLines.first().takeLastWhile { it.isDigit() }.toInt()).map {
            mutableListOf<Char>()
        }
        for (stackLine in stackLines.drop(1)) {
            stackLine.chunked(4).forEachIndexed { index, element ->
                if (element.isBlank()) {
                    return@forEachIndexed
                } else {
                    stacks[index].add(element.trim().removeSurrounding("[", "]").single())
                }
            }
        }

        val instructionRegex = Regex("""move (\d+) from (\d+) to (\d+)""")
        val instructions = rawInstructions.split("\n").map {
            val (amount, from, to) = instructionRegex.find(it)!!.destructured
            Instruction(amount.toInt(), from.toInt(), to.toInt())
        }

        return stacks to instructions
    }

    fun part1(input: String): String {
        val (stacks, instructions) = parse(input)
        instructions.forEach { instruction ->
            stacks[instruction.to - 1].addAll(stacks[instruction.from - 1].takeLast(instruction.amount).reversed())
            stacks[instruction.from - 1].removeLast(instruction.amount)
        }

        return stacks.joinTopElementsToString()
    }

    fun part2(input: String): String {
        val (stacks, instructions) = parse(input)
        instructions.forEach { instruction ->
            stacks[instruction.to - 1].addAll(stacks[instruction.from - 1].takeLast(instruction.amount))
            stacks[instruction.from - 1].removeLast(instruction.amount)
        }

        return stacks.joinTopElementsToString()
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readFile("test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    // Solve puzzle and print result
    val input = utils.readFile()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}