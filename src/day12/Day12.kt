package day12

import Utils
import java.util.*
import kotlin.properties.Delegates

fun main() {
    val utils = Utils(12)

    data class Node(val x: Int, val y: Int, val elevation: Char) {
        var distance by Delegates.notNull<Long>()
    }

    class Map(val starNode: Node, val endNode: Node, val heightMap: List<List<Node>>)

    fun parse(input: List<String>): Map {
        var start: Node? = null
        var end: Node? = null

        val map = input.mapIndexed { lineIndex, line ->
            line.mapIndexed { columnIndex, char ->
                val elevation = when {
                    char.isLowerCase() -> char
                    char == 'S' -> 'a'
                    char == 'E' -> 'z'
                    else -> error("Unexpected input")
                }

                val node = Node(lineIndex, columnIndex, elevation)
                if (char == 'S') {
                    start = node
                } else if (char == 'E') {
                    end = node
                }

                node
            }
        }

        return Map(start!!, end!!, map)
    }

    fun List<List<Node>>.calcDistances(startNode: Node) {
        fun Node.getNeighbours() = listOfNotNull(
            this@calcDistances.getOrNull(this.x - 1)?.getOrNull(this.y)?.takeIf { it.elevation - this.elevation <= 1 },
            this@calcDistances.getOrNull(this.x + 1)?.getOrNull(this.y)?.takeIf { it.elevation - this.elevation <= 1 },
            this@calcDistances.getOrNull(this.x)?.getOrNull(this.y - 1)?.takeIf { it.elevation - this.elevation <= 1 },
            this@calcDistances.getOrNull(this.x)?.getOrNull(this.y + 1)?.takeIf { it.elevation - this.elevation <= 1 }
        )

        val queue = PriorityQueue(compareBy(Node::distance))

        // init
        forEach { line -> line.onEach { it.distance = Long.MAX_VALUE } }
        startNode.distance = 0
        queue.add(startNode)

        // Get current node
        var node: Node? = queue.poll()
        while (node != null) {
            node.getNeighbours().forEach {
                val newCost = node!!.distance + 1
                when {
                    queue.contains(it) && newCost < node!!.distance -> it.distance = newCost

                    it.distance == Long.MAX_VALUE -> {
                        it.distance = newCost
                        queue.add(it)
                    }
                }
            }
            node = queue.poll()
        }
    }

    fun part1(input: List<String>): Long {
        val map = parse(input)
        map.heightMap.calcDistances(map.starNode)
        return map.endNode.distance
    }

    fun part2(input: List<String>): Long {
        val map = parse(input)

        val startPoints = map.heightMap.flatten().filter { it.elevation == 'a' }
        val distances = startPoints.map {
            map.heightMap.calcDistances(it)
            map.endNode.distance
        }

        return distances.min()
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 31L)
    check(part2(testInput) == 29L)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}