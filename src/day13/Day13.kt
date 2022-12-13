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
        return when {
            this is DataList && other is DataList -> {
                when {
                    this.values.isEmpty() || other.values.isEmpty() -> this.values.count() - other.values.count()
                    else ->
                        when (val result = this.values.first().compareTo(other.values.first())) {
                            0 -> DataList(this.values.drop(1)).compareTo(DataList(other.values.drop(1)))
                            else -> result
                        }
                }
            }

            this is DataInt && other is DataInt -> this.value.compareTo(other.value)
            this is DataInt -> this.toDataList().compareTo(other)
            other is DataInt -> this.compareTo(other.toDataList())
            else -> error("Unexpected comparison")
        }
    }
}

class DataList(val values: List<PackageData>) : PackageData()
class DataInt(val value: Int) : PackageData() {
    fun toDataList() = DataList(listOf(this))
}