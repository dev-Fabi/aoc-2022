package day23

import Utils

fun main() {
    val utils = Utils(23)

    fun <T> List<T>.shift(): List<T> {
        val list = this.toMutableList()
        list.add(list.removeFirst())
        return list
    }

    fun Elf.proposeMove(elves: Set<Elf>, moveOrder: List<Move>): Elf {
        if (this.adjacents.values.toSet().all { !elves.contains(it) }) return this

        val move = moveOrder.firstOrNull { move ->
            move.freeDirections.all { !elves.contains(this.adjacents[it]) }
        }

        return if (move == null) {
            this
        } else {
            this.move(move.direction)
        }
    }

    fun parseMap(input: List<String>): Set<Elf> = buildSet {
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, point ->
                if (point == '#') {
                    add(Elf(x, y))
                }
            }
        }
    }

    fun simulateRound(elves: Set<Elf>, moveOrder: List<Move>): Pair<Set<Elf>, List<Move>> {

    }

    fun part1(input: List<String>): Int {
        val initialMoveOrder = listOf(
            Move.North,
            Move.South,
            Move.West,
            Move.East
        )

        val initial = Pair(parseMap(input), initialMoveOrder)

        val (end, _) = (1..10).fold(initial) { (elves, moveOrder), _ ->
            val proposedMoves = elves.map { it to it.proposeMove(elves, moveOrder) }

            val moveCount = proposedMoves.groupingBy { it.second }.eachCount()

            val (move, stay) = proposedMoves.partition { moveCount[it.second]!! == 1 }

            return@fold move.map { it.second }.toSet() + stay.map { it.first }.toSet() to moveOrder.shift()
        }

        val xRange = end.minOf { it.x }..end.maxOf { it.x }
        val yRange = end.minOf { it.y }..end.maxOf { it.y }

        return xRange.sumOf { x ->
            yRange.count { y ->
                !end.contains(Elf(x, y))
            }
        }
    }

    fun part2(input: List<String>): Int {
        var moveOrder = listOf(
            Move.North,
            Move.South,
            Move.West,
            Move.East
        )

        var elves = parseMap(input)
        var moved: Boolean
        var counter = 0

        do {
            val proposedMoves = elves.map { it to it.proposeMove(elves, moveOrder) }

            moved = proposedMoves.any { it.first != it.second }

            val moveCount = proposedMoves.groupingBy { it.second }.eachCount()

            val (move, stay) = proposedMoves.partition { moveCount[it.second]!! == 1 }

            elves = move.map { it.second }.toSet() + stay.map { it.first }.toSet()
            moveOrder = moveOrder.shift()
            counter++
        } while (moved)

        return counter
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}

enum class Direction(val deltaX: Int, val deltaY: Int) {
    N(0, -1),
    NE(1, -1),
    E(1, 0),
    SE(1, 1),
    S(0, 1),
    SW(-1, 1),
    W(-1, 0),
    NW(-1, -1)
}

sealed class Move(val direction: Direction, val freeDirections: Set<Direction>) {
    object North : Move(Direction.N, setOf(Direction.N, Direction.NE, Direction.NW))
    object South : Move(Direction.S, setOf(Direction.S, Direction.SE, Direction.SW))
    object West : Move(Direction.W, setOf(Direction.W, Direction.NW, Direction.SW))
    object East : Move(Direction.E, setOf(Direction.E, Direction.NE, Direction.SE))
}

data class Elf(val x: Int, val y: Int) {
    val adjacents: Map<Direction, Elf> by lazy {
        Direction.values().associateWith { move(it) }
    }

    fun move(direction: Direction): Elf = copy(x = x + direction.deltaX, y = y + direction.deltaY)
}