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

// All inputs seem to be contiguous 1..N, so size(input) == N, max of all

typealias Cup = Int
val NOCUP = -1

data class GameState(val cups: List<Cup>, val current: Int)

// The rotation's cheesy and slow but avoids messing with modular arithmetic
// when it comes to wrap-around points

fun rotateToEnd(cups: List<Cup>, cup: Cup): List<Cup> {
    val rotated = cups.toMutableList()
    while (rotated.last() != cup) {
        val tmp = rotated.removeAt(0)
        rotated.add(tmp)
    }
    return rotated
}

fun move(gs: GameState): GameState {
    val cups = gs.cups
    val current = gs.current

    val currentEnds = rotateToEnd(cups, current)
    val picks = currentEnds.slice(0..2)
    val remains = currentEnds.slice(3 until currentEnds.size)

    fun decrLabel(label: Cup) = if (label == 1) cups.size else label - 1

    var destLabel = current
    do {
        destLabel = decrLabel(destLabel)
    } while (destLabel in picks) 

    val destEnds = rotateToEnd(remains, destLabel)
    val inserted = picks + destEnds

    val currentPos = inserted.indexOf(current)
    val nextCurrent = inserted[(currentPos + 1) % inserted.size]
        
    return GameState(inserted, nextCurrent)
}

fun play(input: List<Cup>): List<Cup> {
    var state = GameState(input, input.first())

    for (i in 1..100) {
        state = move(state)
    }

    return state.cups
}

fun canonicalise(cups: List<Cup>): List<Cup> {
    val can = mutableListOf<Cup>()
    val one = cups.indexOf(1)

    for (i in one + 1 until one + cups.size) {
        can.add(cups[i % cups.size])
    }

    return can
}

// Answer = 65432978
fun processPart1() {
    println("Cups $cups")

    val cups = play(cups)
    val can = canonicalise(cups)
    val canString = can.joinToString("")

    println("Part 1 answer - $canString")
}

// Lazy, content-addressable, cyclic list

class LL {

    // Rather than have conventional linked list nodes and an external
    // hashmap for quick access by content value to specific nodes, I went
    // this way as a mechanism for laziness i.e. hash map keys are content 
    // values and entries are the link(s). Slow for sequential traversal but
    // we hardly do sequential traversal

    data class Links(var next: Cup) // Adding a default initialiser here crashes kotlinc

    val map = hashMapOf<Cup, Links>()
    val max: Cup

    constructor(cups: List<Cup>, max: Cup) {
        this.max = max

        var maxlinks = Links(NOCUP)
        setLinks(max, maxlinks)
        
        var lastc = max
        for (c in cups) {
            val clinks = Links(NOCUP)
            setLinks(c, clinks)
            links(lastc).next = c
            
            lastc = c
        }

        // We need to seed this with 1+ max input cups so that we can
        // lazily populate in sequence up from here
        val lazystart = cups.size + 1
        links(lastc).next = lazystart

        maxlinks.next = cups[0]
    }

    // This is where we unwind the laziness if needs be
    fun links(c: Cup): Links {
        var links = map[c]

        if (links == null) {
            links = Links(c + 1)
            setLinks(c, links)
        }

        return links
    }

    fun setLinks(c: Cup, links: Links) {
        map[c] = links
    }

    fun next(c: Cup) = links(c).next

    // For convenience, the removed three remain internally linked together and
    // in the map, just orphanes in terms of linkage
    fun pick(after: Cup, n: Int): List<Cup> {
        val picked = mutableListOf<Cup>()

        var cursor = next(after)
        for (i in 1..n) {
            picked.add(cursor)
            cursor = next(cursor)
        }

        links(after).next = cursor
        links(cursor)
        
        return picked
    }

    // This relies on the above i.e. they're still internally linked
    fun insert(after: Cup, picks: List<Cup>) {
        val afterNext = next(after)

        links(after).next = picks.first()
        links(picks.last()).next = afterNext
    }

}

data class GameState2(val cups: LL, val current: Cup)

fun move2(gs: GameState2): GameState2 {
    val cups = gs.cups
    val current = gs.current

    // Mutates cups
    val picks = cups.pick(current, 3)

    fun decrLabel(label: Cup) = if (label == 1) cups.max else label - 1

    var destLabel = current
    do {
        destLabel = decrLabel(destLabel)
    } while (destLabel in picks)

    // Mutates cups
    cups.insert(destLabel, picks)

    val nextCurrent = cups.next(current)
    return GameState2(cups, nextCurrent)
}

fun play2(input: List<Cup>, max: Cup): GameState2 {
    var state = GameState2(LL(input, max), input[0])
    for (i in 1..(MIL * 10)) {
        state = move2(state)
    }
    return state
}

val MIL = 1000000

// Answer = 287230227046
fun processPart2() {
    println("Cups $cups")

    val state = play2(cups, MIL)

    val r1 = state.cups.next(1)
    val r2 = state.cups.next(r1)

    println("Part 2 answer - ${r1.toLong() * r2.toLong()} = $r1 * $r2")
}

val data = parseLines()
val cups = data[0].map { it.toString().toInt() }

processPart1()
processPart2()
