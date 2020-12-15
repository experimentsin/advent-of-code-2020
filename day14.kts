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

/// Solution

// Nesting and aliasing works around a bug in the kts compilation process
sealed class Op {
    
    data class Mask(val mask: String) : Op() {
        val andMask = mask.map {
            // Used to clear
            when (it) {
                '0'  -> '0'
                else -> '1'
            }
        }.joinToString("").toLong(2)

        val orMask = mask.map {
            // Used to set
            when (it) {
                '1'  -> '1'
                else -> '0'
            }
        }.joinToString("").toLong(2)

        val floatingBits = mask.toList().asReversed().mapIndexed { i, c ->
            // Used to iterate through alternatives                                                                    
            when (c) {
                'X'  -> i
                else -> -1
            }
        }.filter { it >= 0 }
    }
    
    data class Write(val addr: Long, val value: Long) : Op()
}
typealias Mask = Op.Mask
typealias Write = Op.Write

fun parseInstr(line: String): Op {
    val maskParse = destructureWithRegex(line, """mask \= ([01X]+)""")
    if (maskParse !== null) {
        val (mask) = maskParse
        return Mask(mask)
    }

    val (addr, value) = destructureWithRegex(line, """mem\[(\d+)\] = (\d+)""") ?: throw Error("Bad line $line")
    return Write(addr.toLong(), value.toLong())
}

data class State(val mem: HashMap<Long, Long> = hashMapOf<Long, Long>(), var mask: Mask? = null)

fun State.write(addr: Long, value: Long) {
    mem[addr] = value
}

fun State.execute(p: List<Op>) {
    for (op in p) {
        when (op) {
            is Mask -> {
                mask = op
            }
            is Write -> {
                var value = op.value
                value = value and mask!!.andMask
                value = value or mask!!.orMask
                write(op.addr, value)
            }
        }
    }
}

// Answer - 15514035145260
fun processPart1() {
    // println("Prog $prog")
    val s = State()
    s.execute(prog)

    val memsum = s.mem.values.sum()
    println("Part 1 sum $memsum")
}

fun State.execute2(p: List<Op>) {

    fun forEachCombo(bits: List<Int>, addr: Long, action: (Long) -> Unit) {
        if (bits.size == 0) {
            action(addr)
            return
        }

        val bit = bits[0]
        val rest = bits.drop(1)

        val bitv = 1L shl bit
        forEachCombo(rest, addr and bitv.inv(), action) // force clear
        forEachCombo(rest, addr or bitv, action) // force set
    }
    
    for (op in p) {
        when (op) {
            is Mask -> {
                mask = op
            }
            is Write -> {
                var addr = op.addr

                addr = addr or mask!!.orMask

                forEachCombo(mask!!.floatingBits, addr, { addrvariant ->
                    write(addrvariant, op.value)
                })
            }
        }
    }
}

// Answer - 3926790061594
fun processPart2() {
    // println("Prog $prog")
    val s = State()
    s.execute2(prog)

    val memsum = s.mem.values.sum()
    println("Part 2 sum $memsum")
}

val data = parseLines()
val prog = data.map(::parseInstr)
processPart1()
processPart2()
