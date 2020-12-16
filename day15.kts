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

/// Solution

// Answer - 1111
fun processPart1() {
    println("Starting: $startingNumbers")

    val history = startingNumbers.dropLast(1).toMutableList()
    var mostRecent = startingNumbers.last()

    while (true) {
        val turn = history.size + 2
        if (turn > 2020) break
        
        print("Turn $turn: ") 
        val i = history.indexOfLast { it == mostRecent }

        if (i < 0) {
            history.add(mostRecent)
            mostRecent = 0L
            println("Speak $mostRecent")
            continue
        }

        history.add(mostRecent)
        mostRecent = (history.size - i - 1).toLong()
        println("Speak $mostRecent")
    }
}

// Thoughts...
//
// Well, we can iterate that high (30000000) in a reasonable time, we just need to be able to
// keep the space used under control for the history and the complexity of the history
// lookup constant

// This works but feels a bit like cheating because you need a couple of GB of heap headroom
// to run it in. For kotlinc to have that much headroom running a .kts, you seem to need to
// configure the underlying JVM via an env variable e.g. export JAVA_OPTS=-Xmx8g

// Answer - 48568
fun processPart2() {
    println("Starting: $startingNumbers")

    val TARGET_ITER = 30000000L
    val LFREQ = 100000

    // Maps value to most recent turn appearance
    // We also keep a running total of theoretical history size = turn - 1
    val history = hashMapOf<Long, Long>()
    var historySize = 0L
    var mostRecent = -1L

    fun commit(n: Long) {
        history[n] = historySize + 1
        ++historySize
    }

    fun speak(n: Long) {
        commit(mostRecent)
        mostRecent = n

        if (historySize % LFREQ == 0L) println("Speak $mostRecent")
    }

    fun mostRecentTurn(n: Long): Long {
        return history[n] ?: -1L
    }

    startingNumbers.forEach(::speak)

    while (true) {
        val turn = historySize + 1
        if (turn > TARGET_ITER) break
        
        if (turn % LFREQ == 0L) print("Turn $turn: ") 
        val lastTurn = mostRecentTurn(mostRecent)

        if (lastTurn < 0L) {
            speak(0L)
            continue
        }

        speak(turn - lastTurn)
    }
}

val data = parseLines()
val startingNumbers = data[0].split(",").map { it.toLong() }
processPart1()
processPart2()
