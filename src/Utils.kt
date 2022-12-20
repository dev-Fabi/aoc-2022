import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

class Utils(val day: Int) {
    private val dayString: String = day.toString().padStart(2, '0')

    /**
     * Reads lines from the given input txt file.
     */
    fun readLines(suffix: String? = null) = getFile(suffix).readLines()

    /**
     * Reads the complete text from given input file.
     */
    fun readFile(suffix: String? = null) = getFile(suffix).readText()

    private fun getFile(suffix: String?): File {
        val fileName = StringBuilder("Day${dayString}")
        suffix?.let { fileName.append("_$suffix") }
        fileName.append(".txt")

        return File("src/day$dayString", fileName.toString())
    }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

/**
 * Converts string list to int list
 */
fun List<String>.asInt(): List<Int> = map { it.toInt() }

/**
 * Returns a progression from this value up/down to the specified to value.
 * Including both, this and to value
 */
infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}

/**
 * Performs the given action on each element of a 2D list, providing sequential index with the element.
 * @param action function that takes the row and column index (as pair) of an element and the element itself and performs the action on the element
 */
fun <T> List<List<T>>.forEach2D(action: (index: Pair<Int, Int>, element: T) -> Unit) {
    this.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, element ->
            action(rowIndex to columnIndex, element)
        }
    }
}

/**
 * Increases the value of the key in a map
 */
fun <K> MutableMap<K, Int>.increase(key: K, by: Int = 1) = set(key, getOrDefault(key, 0).plus(by))

/**
 * Increases the value of the key in a map
 */
fun <K> MutableMap<K, Long>.increase(key: K, by: Long = 1) = set(key, getOrDefault(key, 0).plus(by))

infix fun Int.divisibleBy(other: Int): Boolean = this % other == 0
infix fun Long.divisibleBy(other: Long): Boolean = this % other == 0L

fun Int.nextIndexOf(collection: Collection<*>): Int = (this + 1) % collection.size
fun Int.nextIndexOf(collection: CharSequence): Int = (this + 1) % collection.length