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
// If Kotlin could intersect infinite ranges like Dylan and Python can...

// Not actually guaranteed to converge unless all the cycles are coprime
// It seems all inputs are actually prime numbers

// Solve delta cycles?
// Pairwise, A vs B, solve for A and B + b cycle lengths

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

        // As a sanity check we can compute two meets and compare with lcm
        if (meets.size == 1) break
    }

    val cycleLength = lcmOf(ai, bi)
    val cycleStart = meets[0]
    
    println("Sol $cycleStart, length $cycleLength")

    return Pair(cycleStart, cycleLength)
}


// This should just use the above solveCongruences()
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

        // As a sanity check we can compute two meets and compare with lcm
        if (meets.size == 1) break
    }

    println("Meets $meets")
    val cycleLength = lcmOf(ref, bus)
    val cycleStart = meets[0].first
    
    println("Start $cycleStart, length $cycleLength")

    return Pair(cycleStart, cycleLength)
}

// Answer - 842186186521918
fun processPart2() {
    val pairs = mutableListOf<Pair<Long, Long>>()
    for ((i, bus) in buses.withIndex()) {
        if (bus < 0) continue

        pairs.add(Pair(bus.toLong(), i.toLong()))
    }

    // Use the first as a reference and calculate congruences relative to it
    val ref = pairs[0]

    val congruences = mutableListOf<Pair<Long, Long>>()
    
    for (i in 1 until pairs.size) {
        val bus = pairs[i].first
        val offset = pairs[i].second
        val congruence = calculateCycle(ref.first, bus, offset)
        congruences.add(congruence)
    }

    println("Congruences $congruences")

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
