package day17

import Utils
import java.util.*

fun main() {
    val utils = Utils(17)

    fun String.asWrappingSequence() = sequence<Char> {
        var i = 0
        while (true) {
            yield(this@asWrappingSequence[i])
            i = (i + 1) % this@asWrappingSequence.count()
        }
    }

    fun nextShape(round: Long, map: Map<Long, Set<Point>>): Shape {
        val y = (map.keys.maxOrNull() ?: -1) + 4
        return when (round % 5) {
            0L -> Shape.VLine(map, y)
            1L -> Shape.Plus(map, y)
            2L -> Shape.MirroredL(map, y)
            3L -> Shape.HLine(map, y)
            4L -> Shape.Block(map, y)
            else -> error("Should not be possible: $round")
        }
    }

    fun part1(input: String): Long {
        val map = hashMapOf<Long, Set<Point>>()
        val dirs = input.asWrappingSequence().iterator()

        repeat(2022) {
            val current = nextShape(it.toLong(), map)
            do {
                current.move(dirs.next())
            } while (current.fall())
            current.points.groupBy(Point::y).forEach { (y, points) ->
                map[y] = map.getOrPut(y) { setOf() }.plus(points)
            }
        }

        return map.keys.max() + 1
    }

    // Would take way to long.
    fun part2(input: String): Long {
        // y to Points
        val map = mutableMapOf<Long, Set<Point>>()
        val dirs = input.asWrappingSequence().iterator()

        val start = Date().time

        for (i in 0L until 1_000_000_000_000) {
            val current = nextShape(i, map)
            do {
                current.move(dirs.next())
            } while (current.fall())
            current.points.groupBy { it.y }.forEach { (y, points) ->
                map[y] = map.getOrPut(y) { setOf() }.plus(points)
            }

            if (i % 1_000 == 0L) {
                // Don't let the map grow to big. If there is a row where all spaces are taken by a rock we can delete everything below,
                // because no other rock can fall anymore below that line
                map.entries.lastOrNull { it.value.count() == 7 }
                    ?.let {
                        (map.keys.first()..it.key).forEach { index -> map.remove(index) }
                    }
            }
            if (i % 10_000 == 0L) {
                println("$i: ${Date().time - start}ms")
            }
        }

        return map.keys.max() + 1
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readFile("test")
    check(part1(testInput) == 3068L)
    //check(part2(testInput) == 1514285714288)

    // Solve puzzle and print result
    val input = utils.readFile()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    //println("\tPart 2: " + part2(input))
}

data class Point(val x: Long, val y: Long)
sealed class Shape(private val map: Map<Long, Set<Point>>) {
    abstract var points: List<Point>
        protected set

    fun move(dir: Char) {
        val newPoints = mutableListOf<Point>()
        when (dir) {
            '>' -> {
                points.forEach {
                    newPoints.add(it.copy(x = it.x + 1))
                }
            }

            '<' -> {
                points.forEach {
                    newPoints.add(it.copy(x = it.x - 1))
                }
            }

            else -> error("Unknown direction: $dir")
        }

        if (newPoints.all { it.x in (0 until 7) && map[it.y]?.contains(it) != true }) {
            points = newPoints
        }
    }

    fun fall(): Boolean {
        val newPoints = points.map {
            it.copy(y = it.y - 1)
        }

        return if (newPoints.any { it.y < 0 || map[it.y]?.contains(it) == true }) {
            false
        } else {
            points = newPoints
            true
        }
    }

    class VLine(map: Map<Long, Set<Point>>, y: Long, x: Long = 2) : Shape(map) {
        override var points: List<Point> = listOf(
            Point(x, y),
            Point(x + 1, y),
            Point(x + 2, y),
            Point(x + 3, y),
        )
    }

    class HLine(map: Map<Long, Set<Point>>, y: Long, x: Long = 2) : Shape(map) {
        override var points: List<Point> = listOf(
            Point(x, y),
            Point(x, y + 1),
            Point(x, y + 2),
            Point(x, y + 3),
        )
    }

    class Plus(map: Map<Long, Set<Point>>, y: Long, x: Long = 2) : Shape(map) {
        override var points: List<Point> = listOf(
            Point(x + 1, y),
            Point(x, y + 1),
            Point(x + 1, y + 1),
            Point(x + 2, y + 1),
            Point(x + 1, y + 2),
        )
    }

    class MirroredL(map: Map<Long, Set<Point>>, y: Long, x: Long = 2) : Shape(map) {
        override var points: List<Point> = listOf(
            Point(x, y),
            Point(x + 1, y),
            Point(x + 2, y),
            Point(x + 2, y + 1),
            Point(x + 2, y + 2),
        )
    }

    class Block(map: Map<Long, Set<Point>>, y: Long, x: Long = 2) : Shape(map) {
        override var points: List<Point> = listOf(
            Point(x, y),
            Point(x + 1, y),
            Point(x, y + 1),
            Point(x + 1, y + 1),
        )
    }
}