/// Boilerplate

import kotlin.math.*

fun parseLines(): List<String> {
    var lines = mutableListOf<String>()

    while (true) {
        val line = readLine()
        if (line == null) return lines
        lines.add(line.trim())
    }
}

fun parseLinesAsGrid(): List<List<Char>> {
    return parseLines().map({ it.toMutableList() })
}

fun <T>parseLinesAsGridAndMap(mapper: (c: Char) -> T): List<List<T>> {
    return parseLines().map({ it.map(mapper).toMutableList() })
}

fun parseAndJoinLines(sep: String = ""): String {
    return parseLines().joinToString(sep)
}


fun parseLineGroups(): List<List<String>> {
    val groups = mutableListOf<List<String>>()
    var groupLines = mutableListOf<String>()

    fun commitGroup() {
        if (!groupLines.isEmpty()) {
            groups.add(groupLines)
            groupLines = mutableListOf<String>()
        }
    }

    while (true) {
        val line = readLine()

        if (line == null) {
            commitGroup()
            return groups
        }

        val trimmed = line.trim()

        if (trimmed.isEmpty()) {
            commitGroup()
            continue
        }

        groupLines.add(line.trim())
    }
}

fun parseAndJoinLineGroups(sep: String = ""): List<String> {
    return parseLineGroups().map({ it.joinToString(sep) })
}

val REGEX_CACHE = hashMapOf<String, Regex>()
fun destructureWithRegex(input: String, pattern: String): MatchResult.Destructured? {
    var regex = REGEX_CACHE[pattern]
    if (regex === null) {
	regex = Regex(pattern)
	REGEX_CACHE[pattern] = regex
    }

    val matchResult = regex.matchEntire(input)
    if (matchResult === null) return null
    return matchResult.destructured
}

fun gcdOf(a: Long, b: Long): Long {
    if (b == 0L) return a
    return gcdOf(b, a % b)
}

fun lcmOf(vararg n: Long): Long {
    var acc = n[0]
    for (i in 1 until n.size) {
        acc = (n[i] * acc) / gcdOf(n[i], acc)
    }
    return acc; 
}

/// Solution

fun playRound(d1: List<Int>, d2: List<Int>): Pair<List<Int>, List<Int>> {
    if (d1.size == 0 || d2.size == 0) throw Error("Empty deck $d1 $d2")

    val t1 = d1.first()
    val t2 = d2.first()

    if (t1 > t2) {
        return(Pair(d1.drop(1) + t1 + t2, d2.drop(1)))
    } else {
        return(Pair(d1.drop(1), d2.drop(1) + t2 + t1))
    }

}

fun score(d: List<Int>): Long {
    var total = 0L
    for ((i, c) in d.reversed().withIndex()) {
        total += c * (i + 1)
    }
    return total
}

// Answer - 29764
fun processPart1() {
    // println("P1 $player1")
    // println("P2 $player2")

    var d1 = player1
    var d2 = player2
    while (true) {
        if (d1.size == 0 || d2.size == 0) break
        val (nd1, nd2) = playRound(d1, d2)
        // println("$nd1")
        // println("$nd2")
        d1 = nd1
        d2 = nd2
    }

    val wind = if (d1.size == 0) d2 else d1
    
    println("Deck1 $d1")
    println("Deck2 $d2")

    val sc = score(wind)
    println("Part 1 answer - $sc is the score for for $wind")
}

fun playRound2(history: HashSet<Pair<List<Int>, List<Int>>>, d1: List<Int>, d2: List<Int>): Triple<List<Int>, List<Int>, Int> {
    if (d1.size == 0 || d2.size == 0) throw Error("Empty deck $d1 $d2")

    val state = Pair(d1, d2)
    if (state in history) {
        return(Triple(d1, d2, 0))
    } else {
        history.add(state)
    }

    val t1 = d1.first()
    val t2 = d2.first()

    if (d1.size - 1 >= t1 && d2.size - 1 >= t2) {
        val (_, _, winner) = playGame(d1.slice(1 .. t1), d2.slice(1 .. t2))
        if (winner == 0) {
            return(Triple(d1.drop(1) + t1 + t2, d2.drop(1), -1))
        } else {
            return(Triple(d1.drop(1), d2.drop(1) + t2 + t1, -1))
        }
    } else {
        if (t1 > t2) {
            return(Triple(d1.drop(1) + t1 + t2, d2.drop(1), -1))
        } else {
            return(Triple(d1.drop(1), d2.drop(1) + t2 + t1, -1))
        }
    }

}

fun playGame(player1: List<Int>, player2: List<Int>): Triple<List<Int>, List<Int>, Int> {
    // println("playGame $player1 $player2")
    val history = hashSetOf<Pair<List<Int>, List<Int>>>()

    var d1 = player1
    var d2 = player2
    
    while (true) {
        if (d1.size == 0) return(Triple(d1, d2, 1))
        if (d2.size == 0) return(Triple(d1, d2, 0))
        
        // println("playRound $d1 $d2")
        val (nd1, nd2, winner) = playRound2(history, d1, d2)
        // println("playRound $d1 $d2: $winner")
        if (winner >= 0) {
            // println("playGame $player1 $player2: $winner")
            return(Triple(nd1, nd2, winner))
        }
        // println("$nd1")
        // println("$nd2")
        d1 = nd1
        d2 = nd2
    }
}

// Answer - 32588
fun processPart2() {
    val (d1, d2, winner) = playGame(player1, player2)
    println("Winner: player${winner + 1}")
    println("Deck1 $d1")
    println("Deck2 $d2")

    val winscore = if (winner == 0) score(d1) else score (d2)
    println("Part 2 answer - $winscore is the winning score")
}

val inputGroups = parseLineGroups()
val player1 = inputGroups[0].drop(1).map { it.toInt() }
val player2 = inputGroups[1].drop(1).map { it.toInt() }

processPart1()
processPart2()
