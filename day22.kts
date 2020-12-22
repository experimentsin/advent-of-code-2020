/// Boilerplate

import kotlin.math.*

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

/// Solution

typealias Deck = List<Int>

fun playRound(d1: Deck, d2: Deck): Pair<Deck, Deck> {
    if (d1.size == 0 || d2.size == 0) throw Error("Empty deck $d1 $d2")

    val t1 = d1.first()
    val t2 = d2.first()

    if (t1 > t2) {
        return(Pair(d1.drop(1) + t1 + t2, d2.drop(1)))
    } else {
        return(Pair(d1.drop(1), d2.drop(1) + t2 + t1))
    }

}

fun score(d: Deck): Long {
    var total = 0L
    for ((i, c) in d.reversed().withIndex()) {
        total += c * (i + 1)
    }
    return total
}

// Answer - 29764
fun processPart1() {
    var d1 = player1
    var d2 = player2
    val wind: Deck

    while (true) {
        if (d1.size == 0) {
            wind = d2
            break
        }
        if (d2.size == 0) {
            wind = d1
            break
        }

        val (nd1, nd2) = playRound(d1, d2)
        d1 = nd1
        d2 = nd2
    }

    println("Deck1 $d1")
    println("Deck2 $d2")

    val sc = score(wind)
    println("Part 1 answer - $sc is the score for $wind")
}

typealias GameState = Pair<Deck, Deck>
typealias Result = Triple<Deck, Deck, Player>
enum class Player { PLAYER1, PLAYER2, NOPLAYER }

fun playRound2(history: HashSet<GameState>, d1: Deck, d2: Deck): Result {
    if (d1.size == 0 || d2.size == 0) throw Error("Empty deck $d1 $d2")

    val state = GameState(d1, d2)
    if (state in history) {
        return(Result(d1, d2, Player.PLAYER1))
    } else {
        history.add(state)
    }

    val t1 = d1.first()
    val t2 = d2.first()


    val winner =
        if (d1.size - 1 >= t1 && d2.size - 1 >= t2) {
            val (_, _, winner) = playGame(d1.slice(1 .. t1), d2.slice(1 .. t2))
            winner
        } else {
            if (t1 > t2) Player.PLAYER1 else Player.PLAYER2
        }

    when (winner) {
        Player.PLAYER1 -> return(Result(d1.drop(1) + t1 + t2, d2.drop(1), Player.NOPLAYER))
        Player.PLAYER2 -> return(Result(d1.drop(1), d2.drop(1) + t2 + t1, Player.NOPLAYER))
        Player.NOPLAYER -> throw Error("Unexpected NOPLAYER result from recursive game")
    }
}

fun playGame(player1: Deck, player2: Deck): Result {
    val history = hashSetOf<GameState>()

    var d1 = player1
    var d2 = player2
    
    while (true) {
        if (d1.size == 0) return(Result(d1, d2, Player.PLAYER2))
        if (d2.size == 0) return(Result(d1, d2, Player.PLAYER1))
        
        val (nd1, nd2, winner) = playRound2(history, d1, d2)
        if (winner != Player.NOPLAYER) {
            return(Result(nd1, nd2, winner))
        }
        d1 = nd1
        d2 = nd2
    }
}

// Answer - 32588
fun processPart2() {
    val (d1, d2, winner) = playGame(player1, player2)
    println("Winner: ${winner}")
    println("Deck1 $d1")
    println("Deck2 $d2")

    val winscore = if (winner == Player.PLAYER1) score(d1) else score (d2)
    println("Part 2 answer - $winscore is the winning score")
}

val inputGroups = parseLineGroups()
val player1 = inputGroups[0].drop(1).map { it.toInt() }
val player2 = inputGroups[1].drop(1).map { it.toInt() }

processPart1()
processPart2()
