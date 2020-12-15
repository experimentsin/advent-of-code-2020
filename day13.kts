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

fun lcmOf(n: List<Long>): Long {
    var acc = n[0]
    for (i in 1 until n.size) {
        acc = (n[i] * acc) / gcdOf(n[i], acc)
    }
    return acc; 
}

/// Solution

// Answer - 104
fun processPart1() {
    println("$timestamp $buses")

    var minDep = Int.MAX_VALUE
    var minBus = -1

    for (bus in buses) {
        if (bus < 0) continue
        // val cycles = timestamp / bus
        val rem = timestamp % bus

        val offset = if (rem == 0) 0 else bus - rem
        println("$bus @ $offset")

        val nextDep = timestamp + offset

        if (nextDep < minDep) {
            minDep = nextDep
            minBus = bus
        }
    }

    println("Min $minBus @ $minDep, ${(minDep - timestamp) * minBus}")

}

// So another lcm hack, but with the twist that it's not naive perfect alignment you're looking for
// Straight re-alignment to same departure time would just be lcm of the cycles
// Solving simultaneous congruences
// a? * A = b? * B - b = c? * C - c = ...
// A, B, b, C, c etc are known
// a?, b? etc are unknown
// But there are multiple solutions and you want the minimum
// Actually solving for n

// Not actually guaranteed to converge unless all the cycles are coprime
// Solve delta cycles?
// Pairwise, A vs B, solve for A and B + b cycle lengths
// It seems all inputs are prime numbers

fun solveCongruences(a: Long, ai: Long, b: Long, bi: Long): Pair<Long, Long> {
    println("solveCongruences $a $ai, $b $bi")
    var acursor = a
    var bcursor = b

    val meets = mutableListOf<Long>()
    
    while (true) {
        if (acursor < bcursor) {
            val mult = (bcursor - acursor) / ai
            acursor += ai * max(1, mult)
            continue
        }

        if (acursor > bcursor) {
            val mult = (acursor - bcursor) / bi
            bcursor += bi * max(1, mult)
            continue
        }

        // println("Met at $refcursor = $buscursor + $offset")
        meets.add(acursor)

        acursor += ai
        bcursor += bi

        if (meets.size == 2) break
    }

    val cycleLength = meets[1] - meets[0]
    val cycleStart = meets[0]
    
    println("Sol $cycleStart, length $cycleLength")

    return Pair(cycleStart, cycleLength)
}


fun calculateCycle(ref: Long, bus: Long, offset: Long): Pair<Long, Long> {
    println("calculateCycle $ref $bus $offset")
    var refcursor = 0L
    var buscursor = 0L

    val meets = mutableListOf<Pair<Long, Long>>()
    
    while (true) {
        if (buscursor - offset < refcursor) {
            val mult = (refcursor - (buscursor - offset)) / bus
            buscursor += (bus * max(mult, 1))
            continue
        }

        if (buscursor - offset > refcursor) {
            val mult = ((buscursor - offset) - refcursor) / ref
            refcursor += (ref * max(mult, 1))
            continue
        }

        // println("Met at $refcursor = $buscursor + $offset")
        meets.add(Pair(refcursor, buscursor + offset))

        refcursor += ref
        buscursor += bus

        if (meets.size == 2) break
    }

    println("Meets $meets")
    val cycleLength = meets[1].first - meets[0].first
    val cycleStart = meets[0].first
    
    println("Start $cycleStart, length $cycleLength")

    return Pair(cycleStart, cycleLength)
}

// Answer - 842186186521918
fun processPart2() {
    val actual = buses.filter { it >= 0 }.map { it.toLong() }.sorted()
    val lcm = lcmOf(actual)
    println("$actual -> $lcm")

    val pairs = mutableListOf<Pair<Long, Long>>()
    for ((i, bus) in buses.withIndex()) {
        if (bus < 0) continue

        pairs.add(Pair(bus.toLong(), i.toLong()))
    }
    // pairs.sortBy { it.first }

    // Use the first as a reference
    val ref = pairs[0]

    val congruences = mutableListOf<Pair<Long, Long>>()
    
    for (i in 1 until pairs.size) {
        val bus = pairs[i].first
        val offset = pairs[i].second
        val cong = calculateCycle(ref.first, bus, offset)
        congruences.add(cong)
    }

    println("Congruences $congruences")

    var cumul = congruences[0]
    for (i in 1 until congruences.size) {
        cumul = solveCongruences(cumul.first, cumul.second, congruences[i].first, congruences[i].second)
    }
    
    println("Cum $cumul")
}

val data = parseLines()
val timestamp = data[0].toInt()
val buses = data[1].split(",").map { if (it == "x") -1 else it.toInt() }

processPart1()
processPart2()
