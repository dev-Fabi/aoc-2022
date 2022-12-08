package day08

import Utils
import java.lang.Integer.max

fun main() {
    val utils = Utils(8)

    fun <T> Iterable<T>.countWhile(lastInclusive: Boolean = false, predicate: (T) -> Boolean) =
        takeWhile(predicate).count().let {
            if (lastInclusive && it < count()) it.plus(1) else it
        }

    fun part1(input: List<String>): Int {
        val rows = input.map { it.map(Char::digitToInt) }
        var visible = 0

        rows.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, treeHeight ->
                if (
                    rowIndex == 0 || columnIndex == 0 || rowIndex == rows.lastIndex || columnIndex == row.lastIndex || // Outer edge
                    row.subList(0, columnIndex).all { it < treeHeight } || // visible from left
                    row.subList(columnIndex + 1, row.count()).all { it < treeHeight } || // visible from right
                    (0 until rowIndex).all { rows[it][columnIndex] < treeHeight } || // visible from top
                    (rowIndex + 1..rows.lastIndex).all { rows[it][columnIndex] < treeHeight } // visible from bottom
                ) {
                    visible++
                }
            }
        }

        return visible
    }

    fun part2(input: List<String>): Int {
        val rows = input.map { it.map(Char::digitToInt) }
        var bestSceneScore = 0

        rows.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, treeHeight ->
                val currentSceneScore = when {
                    rowIndex == 0 || columnIndex == 0 || rowIndex == rows.lastIndex || columnIndex == row.lastIndex -> 0
                    else -> {
                        val left = row.subList(0, columnIndex).reversed().countWhile(true) { it < treeHeight }
                        val right = row.subList(columnIndex + 1, row.count()).countWhile(true) { it < treeHeight }
                        val top = (0 until rowIndex).reversed().countWhile(true) { rows[it][columnIndex] < treeHeight }
                        val down =
                            (rowIndex + 1..rows.lastIndex).countWhile(true) { rows[it][columnIndex] < treeHeight }

                        left * right * top * down
                    }
                }

                bestSceneScore = max(bestSceneScore, currentSceneScore)
            }
        }

        return bestSceneScore
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}