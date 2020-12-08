/// Boilerplate

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

/// Solution

data class Op(val name: String, val arg: Int)

data class State(var ops: List<Op>, var pc: Int = 0, var acc: Int = 0)

enum class ExitReason { NORMAL, ABNORMAL }

fun execute(s: State): ExitReason {

    val opCounts = ops.map({ 0 }).toMutableList()

    while (true) {
	if (s.pc >= s.ops.size) return ExitReason.NORMAL

	if (opCounts[s.pc]++ > 0) return ExitReason.ABNORMAL
	
	val op = s.ops[s.pc]
	when (op.name) {
	    "nop" -> {
		++s.pc
	    }

	    "acc" -> {
		s.acc += op.arg
		++s.pc
	    }

	    "jmp" -> {
		s.pc += op.arg
	    }
	}
    }
}

fun parseOp(line: String): Op {
    val (name, arg) = destructureWithRegex(line, """([a-z]+) ([+\-]\d+)""") ?: throw Error("Failed to parse $line")
    return Op(name, arg.toInt())
}

// Answer - 1451 @ 15mins
fun processPart1() {
    // println("Ops: $ops")
    val s = State(ops)
    execute(s)

    println("Part 1 answer: ${s.acc}")
}

// Answer - 1160 @ 26mins
fun processPart2() {
    for (i in ops.indices) {
	val op = ops[i]

	val replacement = when (op.name) {
	    "nop" -> Op("jmp", ops[i].arg)
	    "jmp" -> Op("nop", ops[i].arg)
	    else -> continue
	}
	val patchable = ops.toMutableList()
	patchable[i] = replacement

	val s = State(patchable)
	val exit = execute(s)
	if (exit == ExitReason.NORMAL) {
	    // println("Patched $i exit")
	    println("Part 2 answer: ${s.acc}")
	    break
	}
    }
}

val data = parseLines()
val ops = data.map(::parseOp)
processPart1()
processPart2()
