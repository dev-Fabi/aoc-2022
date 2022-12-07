package day07

import Utils
import kotlin.properties.Delegates

fun main() {
    val utils = Utils(7)

    fun IFile.calcSize(): Long {
        return when (this) {
            is File -> this.size
            is Directory -> this.children.sumOf { it.calcSize() }.also { this.size = it }
        }
    }

    fun Directory.findSmallerOrEqualThan(size: Int): List<Directory> {
        val children = this.childDirs.flatMap { it.findSmallerOrEqualThan(size) }
        return if (this.size > size) {
            children
        } else {
            children.plus(this)
        }
    }

    fun Directory.findGreaterOrEqualThan(size: Long): List<Directory> {
        val children = this.childDirs.flatMap { it.findGreaterOrEqualThan(size) }
        return if (this.size < size) {
            children
        } else {
            children.plus(this)
        }
    }

    fun buildDisk(input: List<String>): Directory {
        val root = Directory("/", null)
        var currentDir = root

        for (line in input.drop(1)) {
            when {
                line == "$ ls" -> Unit
                line == "$ cd .." -> {
                    currentDir = currentDir.parent!!
                }

                line.startsWith("$ cd") -> {
                    currentDir = currentDir.childDirs.first { it.name == line.substringAfter("cd ") }
                }

                line.startsWith("dir") -> {
                    currentDir.children.add(Directory(line.substringAfter(" "), currentDir))
                }

                line.first().isDigit() -> {
                    val (size, name) = line.split(" ")
                    currentDir.children.add(File(name, currentDir, size.toLong()))
                }

                else -> error("Unexpected input")
            }
        }

        root.calcSize()

        return root
    }

    fun part1(input: List<String>): Long {
        val root = buildDisk(input)

        return root.findSmallerOrEqualThan(100000).sumOf { it.size }
    }

    fun part2(input: List<String>): Long {
        val root = buildDisk(input)
        val neededSpace = 30000000 - (70000000 - root.size)

        return root.findGreaterOrEqualThan(neededSpace).minOf { it.size }
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 95437L)
    check(part2(testInput) == 24933642L)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}

sealed interface IFile {
    val name: String
    val parent: Directory?
    val size: Long
}

class File(override val name: String, override val parent: Directory, override val size: Long = 0) : IFile

class Directory(override val name: String, override val parent: Directory?) : IFile {
    override var size by Delegates.notNull<Long>()
    val children = mutableListOf<IFile>()
    val childDirs: List<Directory> get() = children.filterIsInstance<Directory>()
}