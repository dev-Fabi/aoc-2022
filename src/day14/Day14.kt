package day14

import Utils
import toward

fun main() {
    val utils = Utils(14)

    data class Point(val x: Int, val y: Int)

    fun parsePaths(input: List<String>): List<List<Point>> = input.map { line ->
        line.split(" -> ")
            .map { point ->
                val (x, y) = point.split(",").map(String::toInt)
                Point(x, y)
            }
    }

    fun parseMap(paths: List<List<Point>>): Array<Array<Char>> {
        val map = Array(paths.flatten().maxOf { it.y } + 1) {
            Array(paths.flatten().maxOf { it.x } + 1) { '.' }
        }

        paths.forEach { points ->
            points.zipWithNext()
                .forEach { (from, to) ->
                    when {
                        from.x != to.x && from.y != to.y -> error("Unexpected diagonal rock path")
                        from.x != to.x -> {
                            (from.x toward to.x).forEach { x ->
                                map[from.y][x] = '#'
                            }
                        }

                        from.y != to.y -> {
                            (from.y toward to.y).forEach { y ->
                                map[y][from.x] = '#'
                            }
                        }

                        else -> error("Wtf: $from -> $to")
                    }
                }
        }

        return map
    }

    fun part1(input: List<String>): Int {
        val map = parseMap(parsePaths(input))

        var sand = Point(500, 0)
        var placedSand = 0
        val blockedChars = listOf('o', '#')
        while (sand.y in map.indices && sand.x in map[0].indices) {
            when {
                map.getOrNull(sand.y + 1)?.getOrNull(sand.x) !in blockedChars -> {
                    sand = Point(sand.x, sand.y + 1)
                }

                map.getOrNull(sand.y + 1)?.getOrNull(sand.x - 1) !in blockedChars -> {
                    sand = Point(sand.x - 1, sand.y + 1)
                }

                map.getOrNull(sand.y + 1)?.getOrNull(sand.x + 1) !in blockedChars -> {
                    sand = Point(sand.x + 1, sand.y + 1)
                }

                else -> {
                    map[sand.y][sand.x] = 'o'
                    placedSand++
                    sand = Point(500, 0)
                }
            }
        }

        println(map.joinToString("\n") { it.joinToString("") })

        return placedSand
    }

    fun part2(input: List<String>): Int {
        val paths = parsePaths(input).let {
            val bottom: List<Point> = listOf(
                Point(0, it.flatten().maxOf(Point::y) + 2),
                Point(it.flatten().maxOf(Point::x) + it.flatten().maxOf(Point::y), it.flatten().maxOf(Point::y) + 2)
            )
            it.plusElement(bottom)
        }

        val map = parseMap(paths)

        var sand = Point(500, 0)
        var placedSand = 0
        while (map[0][500] != 'o') {
            when {
                map.getOrNull(sand.y + 1)?.getOrNull(sand.x) == '.' -> {
                    sand = Point(sand.x, sand.y + 1)
                }

                map.getOrNull(sand.y + 1)?.getOrNull(sand.x - 1) == '.' -> {
                    sand = Point(sand.x - 1, sand.y + 1)
                }

                map.getOrNull(sand.y + 1)?.getOrNull(sand.x + 1) == '.' -> {
                    sand = Point(sand.x + 1, sand.y + 1)
                }

                else -> {
                    map[sand.y][sand.x] = 'o'
                    placedSand++
                    sand = Point(500, 0)
                }
            }
        }

        println(map.joinToString("\n") { it.joinToString("") })

        return placedSand
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}

