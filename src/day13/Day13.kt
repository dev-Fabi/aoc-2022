package day13

import Utils
import kotlinx.serialization.json.*

fun main() {
    val utils = Utils(13)

    fun parse(input: String): DataList {
        fun parseJson(input: JsonElement): PackageData {
            return if (input is JsonArray) {
                val list = buildList {
                    for (i in input) {
                        add(parseJson(i))
                    }
                }
                DataList(list)
            } else {
                input as JsonPrimitive
                DataInt(input.int)
            }
        }

        val list: JsonArray = Json.decodeFromString(JsonArray.serializer(), input)
        return parseJson(list) as DataList
    }

    fun part1(input: String): Int {
        val packages = input.split("\n\n").map { data ->
            val (left, right) = data.split("\n")

            parse(left) to parse(right)
        }

        return packages.withIndex().sumOf { (index, data) ->
            if (data.first < data.second) {
                index + 1
            } else {
                0
            }
        }
    }

    fun part2(input: String): Int {
        val dividers = listOf(
            parse("[[2]]"),
            parse("[[6]]")
        )
        val packages = dividers + input.split("\n").filter { it.isNotBlank() }.map {
            parse(it)
        }

        val sorted = packages.sorted()

        return (sorted.indexOfFirst { it == dividers[0] } + 1) * (sorted.indexOfFirst { it == dividers[1] } + 1)
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readFile("test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    // Solve puzzle and print result
    val input = utils.readFile()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}

sealed class PackageData : Comparable<PackageData> {
    override fun compareTo(other: PackageData): Int {
        if (this is DataInt && other is DataInt) {
            return this.value.compareTo(other.value)
        } else if (this is DataList && other is DataList) {
            if (this.values.isEmpty() && other.values.isNotEmpty()) {
                return -1
            } else if (this.values.isNotEmpty() && other.values.isEmpty()) {
                return 1
            } else if (this.values.isEmpty()) {
                return 0
            }

            val result = this.values.first().compareTo(other.values.first())

            return if (result == 0) {
                DataList(this.values.drop(1)).compareTo(DataList(other.values.drop(1)))
            } else {
                result
            }
        } else if (this is DataInt) {
            return this.toDataList().compareTo(other)
        } else if (other is DataInt) {
            return this.compareTo(other.toDataList())
        }

        error("Unexpected comparison")
    }
}

class DataList(val values: List<PackageData>) : PackageData()
class DataInt(val value: Int) : PackageData() {
    fun toDataList() = DataList(listOf(this))
}