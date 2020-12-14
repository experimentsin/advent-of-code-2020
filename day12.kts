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

data class Vec2(val x: Int, val y: Int)

fun addVec2(v1: Vec2, v2: Vec2) = Vec2(v1.x + v2.x, v1.y + v2.y)


/// Solution

data class Inst(val code: Char, val value: Int)

fun parseInst(line: String): Inst {
    val (code, value) = destructureWithRegex(line, """([A-Z])(\d+)""") ?: throw Error("Failed to parse $line")
    return Inst(code[0], value.toInt())
}

// X >, Y ^
val HEADINGS = listOf(Vec2(0, 1), Vec2(1, 0), Vec2(0, -1), Vec2(-1, 0))

fun addNVec2(v1: Vec2, v2: Vec2, n: Int) = Vec2(v1.x + v2.x * n, v1.y + v2.y * n)

data class NavState(var pos: Vec2, var headingIndex: Int)

fun NavState.navigate(insts: List<Inst>): Vec2 {
    for (inst in insts) {
        when (inst.code) {
            'N' -> pos = addNVec2(pos, HEADINGS[0], inst.value)
            'E' -> pos = addNVec2(pos, HEADINGS[1], inst.value)
            'S' -> pos = addNVec2(pos, HEADINGS[2], inst.value)
            'W' -> pos = addNVec2(pos, HEADINGS[3], inst.value)

            'L' -> for (i in 90..inst.value step 90) headingIndex = (headingIndex + 3) % 4
            'R' -> for (i in 90..inst.value step 90) headingIndex = (headingIndex + 1) % 4
            'F' -> pos = addNVec2(pos, HEADINGS[headingIndex], inst.value)
        }
    }

    return pos
}

// Answer - 508
fun processPart1() {
    // println("Insts $insts")

    val ns = NavState(Vec2(0, 0), 1)
    val fin = ns.navigate(insts)
    val mh = Math.abs(fin.x) + Math.abs(fin.y)

    println("Fin $fin $mh")
}

data class NavState2(var spos: Vec2, var wpos: Vec2)

fun rotateLeft(v: Vec2): Vec2 = Vec2(-v.y, v.x)
fun rotateRight(v: Vec2): Vec2 = Vec2(v.y, -v.x)

fun NavState2.navigate(insts: List<Inst>): Vec2 {
    for (inst in insts) {
        when (inst.code) {
            // Waypoint move
            'N' -> wpos = addNVec2(wpos, HEADINGS[0], inst.value)
            'E' -> wpos = addNVec2(wpos, HEADINGS[1], inst.value)
            'S' -> wpos = addNVec2(wpos, HEADINGS[2], inst.value)
            'W' -> wpos = addNVec2(wpos, HEADINGS[3], inst.value)

            // Waypoint rotate
            'L' -> for (i in 90..inst.value step 90) wpos = rotateLeft(wpos)
            'R' -> for (i in 90..inst.value step 90) wpos = rotateRight(wpos)

            // Move in waypoint direction
            'F' -> spos = addNVec2(spos, wpos, inst.value)
        }
    }

    return spos
}

// Answer - 30761
fun processPart2() {
    // println("Insts $insts")

    val ns = NavState2(Vec2(0, 0), Vec2(10, 1))
    val fin = ns.navigate(insts)
    val mh = Math.abs(fin.x) + Math.abs(fin.y)

    println("Fin $fin $mh")
}

val data = parseLines()
val insts = data.map(::parseInst)
processPart1()
processPart2()
