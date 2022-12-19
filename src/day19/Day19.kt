package day19

import Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.math.max

suspend fun main() {
    val utils = Utils(19)

    fun parseBlueprints(input: List<String>): List<Blueprint> {
        val regex = Regex(
            """Blueprint (\d+): Each ore robot costs (\d+) ore\. Each clay robot costs (\d+) ore\. """ +
                    """Each obsidian robot costs (\d+) ore and (\d+) clay\. Each geode robot costs (\d+) ore and (\d+) obsidian\."""
        )

        return input.map {
            val (
                id,
                oreRobotOre,
                clayRobotOre,
                obsidianRobotOre,
                obsidianRobotClay,
                geodeRobotOre,
                geodeRobotObsidian
            ) = regex.find(it)!!.destructured

            Blueprint(
                id = id.toInt(),
                oreRobotOreCost = oreRobotOre.toInt(),
                clayRobotOreCost = clayRobotOre.toInt(),
                obsidianRobotOreCost = obsidianRobotOre.toInt(),
                obsidianRobotClayCost = obsidianRobotClay.toInt(),
                geodeRobotOreCost = geodeRobotOre.toInt(),
                geodeRobotObsidianCost = geodeRobotObsidian.toInt()
            )
        }
    }

    fun Blueprint.calculateMaxGeodes(minutes: Int): Int {
        val initialStock = Stock(
            ore = 0,
            clay = 0,
            obsidian = 0,
            geode = 0,
            oreRobots = 1,
            clayRobots = 0,
            obsidianRobots = 0,
            geodeRobots = 0
        )

        val initialState = State(minutes, initialStock)

        val states = ArrayDeque<State>()
        states.add(initialState)
        var maxGeodes = 0

        while (states.isNotEmpty()) {
            val current = states.removeLast()
            maxGeodes = max(maxGeodes, current.stock.geode)

            if (current.minute == 0) continue
            if (current.stock.geodePotential(current.minute, this) <= maxGeodes) continue

            val nextStocks = current.stock.getAllPossibleNextStocks(this)
            states.addAll(
                nextStocks.map {
                    current.copy(minute = current.minute - 1, stock = it)
                }
            )
        }

        //println("Blueprint ${this.id}: $maxGeodes geodes")

        return maxGeodes
    }

    // Takes around 3 seconds
    suspend fun part1(input: List<String>): Int = withContext(Dispatchers.Default) {
        val blueprints = parseBlueprints(input)

        blueprints.map { blueprint ->
            async {
                blueprint.id * blueprint.calculateMaxGeodes(24)
            }
        }.sumOf { it.await() }
    }

    // Takes around 20 seconds
    suspend fun part2(input: List<String>): Long = withContext(Dispatchers.Default) {
        val blueprints = parseBlueprints(input.take(3))

        blueprints.map { blueprint ->
            async {
                blueprint.calculateMaxGeodes(32)
            }
        }.fold(1L) { acc, cur ->
            acc * cur.await()
        }
    }

    // Test if implementation meets criteria from the description
    val testInput = utils.readLines("test")
    check(part1(testInput) == 33)
    //check(part2(testInput) == 0)

    // Solve puzzle and print result
    val input = utils.readLines()
    println("Solution day ${utils.day}:")
    println("\tPart 1: " + part1(input))
    println("\tPart 2: " + part2(input))
}

data class State(val minute: Int, val stock: Stock)

class Blueprint(
    val id: Int,
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int,
) {
    val maxOreCost = maxOf(oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, geodeRobotOreCost)
    inline val maxClayCost get() = obsidianRobotClayCost
    inline val maxObsidianCost get() = geodeRobotObsidianCost
}

data class Stock(
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geode: Int,
    val oreRobots: Int,
    val clayRobots: Int,
    val obsidianRobots: Int,
    val geodeRobots: Int,
) {
    fun getAllPossibleNextStocks(blueprint: Blueprint): List<Stock> = buildList {
        getPossibleBuilds(blueprint).forEach {
            add(build(blueprint, it))
        }
    }

    fun geodePotential(leftTime: Int, blueprint: Blueprint): Int {
        val maxPossibleOre = ore + (0..leftTime).sumOf { oreRobots + it }
        val maxPossibleObsidian = obsidian + (0..leftTime).sumOf { obsidianRobots + it }

        val maxPossibleGeodeRobots = minOf(
            leftTime,
            maxPossibleOre / blueprint.geodeRobotOreCost + 1,
            maxPossibleObsidian / blueprint.geodeRobotObsidianCost + 1
        )

        return geode + (1..maxPossibleGeodeRobots).sumOf { geodeRobots + it } - 1
    }

    private fun getPossibleBuilds(blueprint: Blueprint): List<Miner?> = buildList {
        // Always try to build a robot of each type when there are enough resources.
        // Do not build more robots of a type than max of the resource type can be used in one round
        // -> would unnecessarily mine resources which could never be used

        if (ore >= blueprint.geodeRobotOreCost && obsidian >= blueprint.geodeRobotObsidianCost) {
            // When possible to build a geodeRobot always do it (not sure why we can exclude all the other options)
            add(Miner.GEODE)
            return@buildList
        }

        add(null) // build nothing

        if (ore >= blueprint.oreRobotOreCost && oreRobots < blueprint.maxOreCost) {
            add(Miner.ORE)
        }
        if (ore >= blueprint.clayRobotOreCost && clayRobots < blueprint.maxClayCost) {
            add(Miner.CLAY)
        }
        if (ore >= blueprint.obsidianRobotOreCost && clay >= blueprint.obsidianRobotClayCost && obsidianRobots < blueprint.maxObsidianCost) {
            add(Miner.OBSIDIAN)
        }
    }

    private fun build(blueprint: Blueprint, miner: Miner?): Stock {
        return with(this.mine()) {
            when (miner) {
                null -> this
                Miner.ORE -> copy(
                    oreRobots = oreRobots + 1,
                    ore = ore - blueprint.oreRobotOreCost
                )

                Miner.CLAY -> copy(
                    clayRobots = clayRobots + 1,
                    ore = ore - blueprint.clayRobotOreCost
                )

                Miner.OBSIDIAN -> copy(
                    obsidianRobots = obsidianRobots + 1,
                    ore = ore - blueprint.obsidianRobotOreCost,
                    clay = clay - blueprint.obsidianRobotClayCost
                )

                Miner.GEODE -> copy(
                    geodeRobots = geodeRobots + 1,
                    ore = ore - blueprint.geodeRobotOreCost,
                    obsidian = obsidian - blueprint.geodeRobotObsidianCost
                )
            }
        }
    }

    private fun mine() = copy(
        ore = ore + oreRobots,
        clay = clay + clayRobots,
        obsidian = obsidian + obsidianRobots,
        geode = geode + geodeRobots
    )
}

enum class Miner {
    ORE, CLAY, OBSIDIAN, GEODE
}