package day18

import Utils

fun main() {
    val utils = Utils(18)

    data class Cube(val x: Int, val y: Int, val z: Int) {
        val neighbors
            get() = setOf(
                Cube(x + 1, y, z),
                Cube(x - 1, y, z),
                Cube(x, y + 1, z),
                Cube(x, y - 1, z),
                Cube(x, y, z + 1),
                Cube(x, y, z - 1),
            )
    }

    fun parseCubes(input: List<String>) = input.map {
        val (x, y, z) = it.split(",").map(String::toInt)
        Cube(x, y, z)
    }.toHashSet()

    fun part1(input: List<String>): Int {
        val cubes = parseCubes(input)

        return cubes.sumOf { it.neighbors.minus(cubes).count() }
    }

    fun part2(input: List<String>): Int {
        val cubes = parseCubes(input)

        val xRange = cubes.minOf { it.x } - 1..cubes.maxOf { it.x } + 1
        val yRange = cubes.minOf { it.y } - 1..cubes.maxOf { it.y } + 1
        val zRange = cubes.minOf { it.z } - 1..cubes.maxOf { it.z } + 1

        val queue = ArrayDeque<Cube>(listOf())
        val outside = hashSetOf<Cube>()

        queue.add(Cube(0, 0, 0))
        while (queue.isNotEmpty()) {
            queue.removeFirst().neighbors.filter {
                it.x in xRange && it.y in yRange && it.z in zRange && it !in cubes && it !in outside
            }.also {
                queue.addAll(it)
                outside.addAll(it)
            }
        }

        return cubes.sumOf { it.neighbors.intersect(outside).count() }
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(listOf("1,1,1", "2,1,1")) == 10)
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}