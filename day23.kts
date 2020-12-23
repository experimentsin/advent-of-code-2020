/// Boilerplate

import kotlin.math.*
import java.util.*

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

typealias Cup = Int

data class GameState(val cups: List<Cup>, val current: Int)

// All inputs seem to be contguous 1..N, so size(input) == N

fun rotateToStart(cups: List<Cup>, cup: Cup): List<Cup> {
    val rotated = cups.toMutableList()
    while (rotated[0] != cup) {
        val tmp = rotated.removeAt(0)
        rotated.add(tmp)
    }
    return rotated
}

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
    println("picks $picks remains $remains")

    fun decrLabel(label: Cup): Cup {
        return if (label == 1) cups.size else label - 1
    }

    var destLabel = decrLabel(current)
    while (destLabel in picks) {
        destLabel = decrLabel(destLabel)
    }

    println("Dest: $destLabel")

    val destEnds = rotateToEnd(remains, destLabel)
    val inserted = picks + destEnds

    val currentPos = inserted.indexOf(current)
    val nextCurrent = inserted[(currentPos + 1) % inserted.size]
        
    return GameState(inserted, nextCurrent)
}

/*
fun move(gs: GameState): GameState {
    val cups = gs.cups
    val current = gs.current

    fun takePicks(l: List<Cup>, current: Cup): Pair<List<Cup>, List<Cup>> {
        val picked = mutableListOf<Cup>()
        val remains = mutableListOf<Cup>()

        val pickedStart = l.indexOf(current) + 1
        val pickedEnd = pickedStart + 2

        for (i in pickedStart..pickedEnd) {
            picked.add(l[i % l.size])
        }

        for (i in l.indices) {
            if (pickedEnd >= l.size && i <= (pickedEnd % l.size)) continue
            if (i >= pickedStart && i <= pickedEnd) continue
            remains.add(l[i % l.size])
        }

        return Pair(picked, remains)
    }
    
    val (picks, remains) = takePicks(cups, current)
    println("picks $picks remains $remains")

    var destLabel = current - 1
    while (destLabel in picks) {
        destLabel -= 1
    }
    if (destLabel < 1) destLabel = cups.size

    println("Dest: $destLabel")
    
    val destPos = remains.indexOf(destLabel)
    // println("${destPos} ${remains}")
    // val moved = remains.slice(0..destPos) + picks + if (destPos + 1 == remains.size - 1) listOf<Int>() else remains.slice((destPos + 1)..(remains.size - 1))
    val moved = remains.slice(0..destPos) + picks + remains.slice((destPos + 1)..(remains.size - 1))

    val currentPos = moved.indexOf(current)
    val nextCurrent = moved[(currentPos + 1) % moved.size]
        
    return GameState(moved, nextCurrent)
}
*/

fun play(input: List<Cup>): List<Cup> {
    var state = GameState(input, input[0])
    for (i in 1..100) {
        println("Move $i = $state")
        state = move(state)
        println("")
    }
    return state.cups
}

fun canonicalise(cups: List<Cup>): List<Cup> {
    val can = mutableListOf<Cup>()
    val one = cups.indexOf(1)

    println("$cups $one")

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


// Thoughts...

// Maybe a lazy datastructure
// Content addressable also

// HashMap of label to prev/next

val LAZYCUP = -1

// Lazy, content-addresable, cyclic list

class LL() {
    data class Links(var prev: Int = -2, var next: Int = -2)

    val map = hashMapOf<Int, Links>()
    var max = -1 // TODO: Move addContent() to a consructor

    fun addContent(cups: List<Cup>, max: Cup) {
        this.max = max

        var maxlinks = Links(LAZYCUP)
        map[max] = maxlinks
        
        var last = max
        for (c in cups) {
            val clinks = Links(last, LAZYCUP)
            map[c] = clinks
            map[last]!!.next = c
            
            last = c
        }

        val lazystart = cups.size + 1
        map[last]!!.next = lazystart

        maxlinks.next = cups[0]
    }

    fun ensure(c: Cup): Links {
        var links = map[c]

        if (links == null) {
            links = Links(c - 1, c + 1)
            map[c] = links
        }

        return links
    }

    fun next(c: Cup): Cup {
        return ensure(c).next
    }

    fun prev(c: Cup): Cup {
        return ensure(c).prev
    }

    // The removed three remain interally linked together
    fun pick3(after: Cup): List<Cup> {
        val picked = mutableListOf<Cup>()
        var cursor = next(after)
        for (i in 1..3) {
            picked.add(cursor)
            cursor = next(cursor)
        }

        map[after]!!.next = cursor

        ensure(cursor)
        map[cursor]!!.prev = after
        
        return picked
    }

    fun insert3(after: Cup, picks: List<Cup>) {
        val afterNext = next(after)

        map[after]!!.next = picks.first()
        map[picks.first()]!!.prev = after

        map[afterNext]!!.prev = picks.last()
        map[picks.last()]!!.next = afterNext
    }

    fun log() {
        /*
        println("Logging: keys ${map.keys} vals ${map.values}")
         */
        for ((k, v) in map) {
            println("$k: $v")
        }

        val vals = mutableListOf<Cup>()

        var start = 1
        var cursor = start
        while (true) {
            if (cursor < 0) break
            vals.add(cursor)

            val cursorlinks = map[cursor] ?: break
            cursor = cursorlinks.next
        }

        start = map[1]!!.prev
        cursor = start
        while (true) {
            if (cursor < 0) break
            vals.add(0, cursor)

            val cursorlinks = map[cursor] ?: break
            cursor = cursorlinks.prev
        }

        println("$vals")

    }

}


data class GameState2(val cups: LL, val current: Int)

fun move2(gs: GameState2): GameState2 {
    val cups = gs.cups
    val current = gs.current

    // Modifies cups
    val picks = cups.pick3(current)

    // println("picks $picks")

    fun decrLabel(label: Cup): Cup {
        return if (label == 1) cups.max else label - 1
    }
    var destLabel = decrLabel(current)
    while (destLabel in picks) {
        destLabel = decrLabel(destLabel)
    }

    // println("Dest: $destLabel")

    cups.insert3(destLabel, picks)
    val nextCurrent = cups.next(current)
        
    return GameState2(cups, nextCurrent)
}

fun play2(input: List<Cup>, max: Int): GameState2 {
    val ll = LL()
    ll.addContent(input, max)
    
    var state = GameState2(ll, input[0])
    for (i in 1..(MIL * 10)) {
        // println("Move $i = $state")
        // ll.log()
        state = move2(state)
        // println("")
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
    println("$r1 $r2 = ${r1.toLong() * r2.toLong()}")
    
}

val data = parseLines()
val cups = data[0].map { it.toString().toInt() }

// processPart1()
processPart2()
