package day11

import Utils

fun main() {
    val utils = Utils(11)

    fun parse(input: String): Map<Int, Monkey> {
        val monkeyMap = mutableMapOf<Int, Monkey>()
        val monkeyBlocks = input.split("\n\n")

        monkeyBlocks.forEachIndexed { index, monkeyDef ->
            val lines = monkeyDef.split("\n")

            val operationNumberString = lines[2].substringAfterLast(" ")
            val operation = if (lines[2].contains("*")) {
                Multiply(operationNumberString)
            } else if (lines[2].contains("+")) {
                Add(operationNumberString)
            } else {
                error("Unexpected operation")
            }

            val test = Test(
                divisibleBy = lines[3].substringAfterLast(" ").toLong(),
                monkeyWhenTrue = lines[4].substringAfterLast(" ").toInt(),
                monkeyWhenFalse = lines[5].substringAfterLast(" ").toInt()
            )

            val monkey = Monkey(
                items = lines[1].substringAfter(": ").split(", ").mapTo(mutableListOf(), String::toLong),
                operation = operation,
                test = test,
                monkeys = monkeyMap
            )

            monkeyMap[index] = monkey
        }

        return monkeyMap
    }

    fun part1(input: String): Long {
        val monkeys = parse(input)

        repeat(20) {
            monkeys.forEach { (_, monkey) ->
                monkey.inspect { it / 3 }
            }
        }

        val (first, second) = monkeys.values.sortedByDescending { it.inspectionCount }.take(2)

        return first.inspectionCount * second.inspectionCount
    }

    fun part2(input: String): Long {
        val monkeys = parse(input)
        val commonDivider = monkeys.values.map { it.test.divisibleBy }.fold(1L) { a, b -> a * b }

        repeat(10000) {
            monkeys.forEach { (_, monkey) ->
                monkey.inspect { it % commonDivider }
            }
        }

        val (first, second) = monkeys.values.sortedByDescending { it.inspectionCount }.take(2)

        return first.inspectionCount * second.inspectionCount
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readFile("test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158)

    // Solve puzzle and print result
    val input = utils.readFile()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}

sealed class Operation(private val numberString: String) {
    private val number by lazy { numberString.toLong() }
    abstract fun execute(old: Long): Long

    fun getNumber(old: Long): Long {
        return if (numberString == "old") {
            old
        } else {
            number
        }
    }
}

class Multiply(numberString: String) : Operation(numberString) {
    override fun execute(old: Long): Long = old * getNumber(old)
}

class Add(numberString: String) : Operation(numberString) {
    override fun execute(old: Long): Long = old + getNumber(old)
}

class Test(val divisibleBy: Long, private val monkeyWhenTrue: Int, private val monkeyWhenFalse: Int) {
    fun nextMonkeyNumber(worryLevel: Long): Int {
        return if (worryLevel % divisibleBy == 0L) {
            monkeyWhenTrue
        } else {
            monkeyWhenFalse
        }
    }
}

class Monkey(
    private val items: MutableList<Long>,
    private val operation: Operation,
    val test: Test,
    private val monkeys: Map<Int, Monkey>,
) {
    var inspectionCount: Long = 0

    fun inspect(worryReducer: (newWorryLevel: Long) -> Long) {
        inspectionCount += items.count()
        items.forEach {
            val newWorryLevel = worryReducer(operation.execute(it))
            val nextMonkey = test.nextMonkeyNumber(newWorryLevel)
            monkeys[nextMonkey]!!.items.add(newWorryLevel)
        }
        items.clear()
    }
}