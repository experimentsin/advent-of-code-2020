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

fun step(sn: Long, value: Long) = (value * sn) % 20201227

fun transform(sn: Long, loop: Long): Long {
    var result = 1L
    for (i in 1..loop) result = step(sn, result)
    return result
}

fun findLoop(pubk: Long): Long {
    var value = 1L
    for (loop in 1L..1000000000L) {
        value = step(7, value)
        if (value == pubk) return loop 
    }
    return -1
}

// Answer - 8740494
fun processPart1() {
    println("Pub keys: $pks")

    val dpubk = pks[0]
    val cpubk = pks[1]

    val dl = findLoop(dpubk)
    val cl = findLoop(cpubk)

    println("Door loop = $dl, Card loop = $cl")

    val engkviad = transform(dpubk, cl)
    val engkviac = transform(cpubk, dl)
    if (engkviac != engkviad) throw Error("Mismatched encryption key recovery")

    println("Part 1 answer - ${engkviac} is the encyption key")
}

fun processPart2() {
    // For free
}

val data = parseLines()
val pks = data.map { it.toLong() }
processPart1()
processPart2()
