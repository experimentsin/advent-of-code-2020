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

enum class OpCode { NOP, ACC, JMP }
data class Op(val code: OpCode, val arg: Int)

fun parseOp(line: String): Op {
    val (name, arg) = destructureWithRegex(line, """([a-z]+) ([+\-]\d+)""") ?: throw Error("Failed to parse $line")
    return Op(OpCode.valueOf(name.toUpperCase()), arg.toInt())
}

data class Context(var ops: List<Op>, var pc: Int = 0, var acc: Int = 0)

enum class ExitReason { NORMAL, ABNORMAL }

fun Context.execute(): ExitReason {
    val opCounts = ops.map { 0 }.toMutableList()

    while (true) {
	if (pc >= ops.size) return ExitReason.NORMAL

	if (opCounts[pc]++ > 0) return ExitReason.ABNORMAL
	
	val op = ops[pc]
	when (op.code) {
	    OpCode.NOP -> {
		++pc
	    }

	    OpCode.ACC -> {
		acc += op.arg
		++pc
	    }
	    OpCode.JMP -> {
		pc += op.arg
	    }
	}
    }
}

// Answer - 1451 @ 15mins
fun processPart1() {
    val ops = inputOps

    val c = Context(ops)
    c.execute()

    println("Part 1 answer: ${c.acc}")
}

// Answer - 1160 @ 26mins
fun processPart2() {
    val ops = inputOps

    for ((i, op) in ops.withIndex()) {

	val replacement = when (op.code) {
	    OpCode.NOP -> Op(OpCode.JMP, op.arg)
	    OpCode.JMP -> Op(OpCode.NOP, op.arg)
	    else       -> continue
	}
	val patchable = ops.toMutableList()
	patchable[i] = replacement

	val c = Context(patchable)
	val exit = c.execute()

	if (exit == ExitReason.NORMAL) {
	    println("Part 2 answer: ${c.acc}")
	    break
	}
    }
}

val inputData = parseLines()
val inputOps = inputData.map(::parseOp)
processPart1()
processPart2()
