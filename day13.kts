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

/// Solution

// Answer - 104
fun processPart1() {
    println("$timestamp $buses")

    var minDep = Int.MAX_VALUE
    var minBus = -1

    for (bus in buses) {
        if (bus < 0) continue
        val rem = timestamp % bus

        val offset = if (rem == 0) 0 else bus - rem
        println("$bus @ $offset")

        val nextDep = timestamp + offset

        if (nextDep < minDep) {
            minDep = nextDep
            minBus = bus
        }
    }

    println("Part 1 answer ${(minDep - timestamp) * minBus} from $minBus @ $minDep, ")

}

// Thoughts...

// So another lcm hack, but with the twist that it's not naive perfect alignment you're looking for
// Straight re-alignment to same departure time would just be lcm of the cycles
// Solving simultaneous range congruences
// If Kotlin could intersect infinite ranges like Dylan could...

// Not actually guaranteed to converge unless all the cycles are coprime
// It seems all inputs are actually prime numbers

// Solve delta cycles?
// Pairwise, A vs B, solve for A and B + b cycle lengths

// One obvious(ish) insight that I missed first time round is that it's equivalent
// to offsetting each bus's starting time back by its time delta offset, then
// solving those parallel ranges/congruences conventionally for intersecting at the same
// time t
// So I can lose all the special case code for recasting the problem relative to
// the reference (t delta 0) bus

fun solveCongruences(a: Long, ai: Long, b: Long, bi: Long): Pair<Long, Long> {
    println("solveCongruences $a $ai, $b $bi")
    var acursor = a
    var bcursor = b

    val meets = mutableListOf<Long>()

    // I'm sure you can find this first intersection algorithmically rather
    // than searching for it, I just can't remember how. I think I did implement
    // this once for Dylan range intersection using the CRT.
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

        // As a sanity check we can compute two meets and compare with lcm
        if (meets.size == 1) break
    }

    val cycleLength = lcmOf(ai, bi)
    val cycleStart = meets[0]
    
    println("Sol $cycleStart, length $cycleLength")

    return Pair(cycleStart, cycleLength)
}

// Answer - 842186186521918
fun processPart2() {
    val congruences = mutableListOf<Pair<Long, Long>>()
    for ((i, bus) in buses.withIndex()) {
        if (bus < 0) continue
        congruences.add(Pair(-i.toLong(), bus.toLong()))
    }


    var cumul = congruences[0]
    for (i in 1 until congruences.size) {
        cumul = solveCongruences(cumul.first, cumul.second, congruences[i].first, congruences[i].second)
    }

    
    println("Part two answer ${cumul.first} from $cumul")
}

val data = parseLines()
val timestamp = data[0].toInt()
val buses = data[1].split(",").map { if (it == "x") -1 else it.toInt() }

processPart1()
processPart2()
