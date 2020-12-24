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

class DefaultingHashMap<K, T> : HashMap<K, T> {
    val default: T

    constructor(default: T) { this.default = default }
    constructor(default: T, tocopy: DefaultingHashMap<K, T>) : super(tocopy) { this.default = default }
    constructor(tocopy: DefaultingHashMap<K, T>): this(tocopy.default, tocopy)

    operator override fun get(key: K) = super.get(key) ?: default
}

/// Solution

/*
   NW/\NE
   W|  |E
   SW\/SE
 */

/*
  Hex grid coordinate system?

  Every other line is offset slightly

  Origin 0,0 -> NE -> 0,1 -> NW -> 0,2
                          -> NW -> 1,2
 
  So Y % 2 behaviour varies slightly
 */

data class Vec2(val x: Int, val y: Int)

val E_E = Vec2(1, 0)
val E_SE = Vec2(0, -1)
val E_SW = Vec2(-1, -1)
val E_W = Vec2(-1, 0)
val E_NW = Vec2(-1, 1)
val E_NE = Vec2(0, 1)

val O_E = Vec2(1, 0)
val O_SE = Vec2(1, -1)
val O_SW = Vec2(0, -1)
val O_W = Vec2(-1, 0)
val O_NW = Vec2(0, 1)
val O_NE = Vec2(1, 1)

enum class Colour { WHITE, BLACK }

fun flip(c: Colour): Colour = if (c == Colour.WHITE) Colour.BLACK else Colour.WHITE

val E_DELTAS = listOf( E_E, E_SE, E_SW, E_W, E_NW, E_NE)
val O_DELTAS = listOf( O_E, O_SE, O_SW, O_W, O_NW, O_NE)
enum class Direction {   E,   SE,   SW,   W,   NW,   NE }

fun addDirection(v: Vec2, dir: Direction): Vec2 {
    val deltas = if (v.y % 2 == 0) E_DELTAS else O_DELTAS
    val delta = deltas[dir.ordinal]
    return Vec2(v.x + delta.x, v.y + delta.y)
}

// Answer - 244
fun processPart1() {
    println("${dirPaths}")

    val tileMap = DefaultingHashMap<Vec2, Colour>(Colour.WHITE)
    
    for (path in dirPaths) {
        var cursor = Vec2(0, 0)

        for (d in path) {
            cursor = addDirection(cursor, d)
        }

        tileMap[cursor] = flip(tileMap[cursor])
    }

    val black = tileMap.values.toList().count { it == Colour.BLACK }
    println("Total black: $black")
}

fun processPart2() {
}

fun parseDirPath(line: String): List<Direction> {
    val path = mutableListOf<Direction>()

    var input = line

    outer@
    while (input.length != 0) {

        fun match(d: Direction): Boolean {
            val m = destructureWithRegex(input, "(${d.name.toLowerCase()})" + "(.*)")
            if (m === null) return false

            val (_, rest) = m
            path.add(d)
            input = rest

            return true // consumed
        }

        for (d in Direction.values()) if (match(d)) continue@outer
        throw Error("Bad input $input from $line")
    }

    return path
}

val lines = parseLines()
val dirPaths = lines.map(::parseDirPath)
    
processPart1()
processPart2()
