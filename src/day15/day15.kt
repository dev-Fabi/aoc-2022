package day15

import Utils
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs

fun main() {
    val utils = Utils(15)

    fun parse(input: List<String>): List<Sensor> {
        fun String.getPoints(): List<Int> {
            return Regex("x=(-?\\d+), y=(-?\\d+)").find(this)!!.groupValues.drop(1).map { it.toInt() }
        }

        return input.map { line ->
            val (sensor, beacon) = line.split(":")
            val (sensorX, sensorY) = sensor.getPoints()
            val (beaconX, beaconY) = beacon.getPoints()

            Sensor(sensorX, sensorY, Beacon(beaconX, beaconY))
        }
    }

    fun part1(input: List<String>, row: Int): Int {
        val sensors = parse(input)

        val maxRange = sensors.maxOf { it.distanceToBeacon }
        val maxX = max(sensors.maxOf { it.x }, sensors.maxOf { it.beacon.x }) + maxRange
        val minX = min(sensors.minOf { it.x }, sensors.minOf { it.beacon.x }) - maxRange

        return (minX..maxX).count { x ->
            sensors.any { Point(x, row).inRageOf(it) } && sensors.all { it.beacon != Point(x, row) }
        }
    }

    fun part2(input: List<String>, maxXY: Int): Long {
        error("Not Implemented")
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput, 10) == 26)
    //check(part2(testInput, 20) == 56000011L)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input, 2000000))
    //println("\tPart 2: " + part2(input, 4000000))
}

open class Point(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean {
        if (other !is Point) return false
        return this.x == other.x && this.y == other.y
    }

    override fun hashCode(): Int {
        return x * 31 + y;
    }
}

fun Point.distanceTo(other: Point): Int {
    return abs(this.x - other.x) + abs(this.y - other.y)
}

class Beacon(x: Int, y: Int) : Point(x, y)
class Sensor(x: Int, y: Int, val beacon: Beacon) : Point(x, y) {
    val distanceToBeacon = this.distanceTo(beacon)
}

fun Point.inRageOf(sensor: Sensor): Boolean {
    return this.distanceTo(sensor) <= sensor.distanceToBeacon
}