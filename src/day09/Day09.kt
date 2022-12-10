package day09

import Utils
import kotlin.math.abs

fun main() {
    val utils = Utils(9)

    data class Knot(val x: Int, val y: Int)
    data class Move(val dir: Char, val amount: Int)

    fun List<Knot>.executeMovesCountingTailVisits(rawMoves: List<String>): Int {
        fun parseMoves(input: List<String>): List<Move> {
            return input.map {
                val (dir, amount) = it.split(" ")
                Move(dir.first(), amount.toInt())
            }
        }

        fun Int.reduce() = when {
            this < 0 -> this + 1
            else -> this - 1
        }

        infix fun Knot.follow(other: Knot): Knot {
            val dx = other.x - this.x
            val dy = other.y - this.y

            val move = when {
                abs(dx) == 2 && abs(dy) == 2 -> dx.reduce() to dy.reduce() // diagonal (only for ropes with more than 2 knots)
                abs(dx) == 1 && abs(dy) == 2 -> dx to dy.reduce() // move diagonal /
                abs(dy) == 1 && abs(dx) == 2 -> dx.reduce() to dy // move diagonal \
                dx !in -1..1 -> dx.reduce() to 0 // move horizontal
                dy !in -1..1 -> 0 to dy.reduce() // move vertical
                else -> return this // No move needed
            }

            return Knot(this.x + move.first, this.y + move.second)
        }

        fun List<Knot>.move(dir: Char): List<Knot> {
            val knots = this
            val head = when (dir) {
                'U' -> Knot(knots.first().x, knots.first().y + 1)
                'D' -> Knot(knots.first().x, knots.first().y - 1)
                'R' -> Knot(knots.first().x + 1, knots.first().y)
                'L' -> Knot(knots.first().x - 1, knots.first().y)
                else -> error("Unexpected move")
            }

            val newKnots = mutableListOf(head)

            knots.drop(1).forEach {
                newKnots.add(it follow newKnots.last())
            }

            return newKnots
        }

        var knots = this
        val moves = parseMoves(rawMoves)
        val visited = mutableSetOf(knots.last())

        moves.forEach { (dir, amount) ->
            repeat(amount) {
                knots = knots.move(dir)
                visited.add(knots.last())
            }
        }

        return visited.count()
    }

    fun buildRope(length: Int) = buildList {
        repeat(length) {
            add(Knot(0, 0))
        }
    }

    fun part1(input: List<String>): Int {
        return buildRope(2).executeMovesCountingTailVisits(input)
    }

    fun part2(input: List<String>): Int {
        return buildRope(10).executeMovesCountingTailVisits(input)
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)
    check(part2(utils.readLines("test2")) == 36)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}