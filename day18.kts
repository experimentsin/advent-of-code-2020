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

sealed class Expr {
    abstract fun eval(): Long
    
    data class Num(val value: Long): Expr() {
        override fun eval() = value
    }

    abstract class Op: Expr() {
        override fun eval() = throw Error("Tried to eval $this")
        abstract fun call(arg1: Long, arg2: Long): Long
    }

    class Plus: Op() {
        override fun call(arg1: Long, arg2: Long) = arg1 + arg2
        override fun toString(): String = "+"
    }

    class Times: Op() {
        override fun call(arg1: Long, arg2: Long) = arg1 * arg2
        override fun toString(): String = "*"
    }

    class Call(val op: Op, val arg1: Expr, val arg2: Expr): Expr() {
        override fun eval() = op.call(arg1.eval(), arg2.eval())
        override fun toString(): String = "$op($arg1, $arg2)"
    }

}

fun leftToRightPrecedence(l: List<Expr>): Expr {
    if (l.size == 1) return l[0]

    val fop = l.indexOfFirst { it is Expr.Op }
    if (fop >= 0) {
        return leftToRightPrecedence(l.slice(0..(fop - 2)) + // Should always be empty in L to R
                                     Expr.Call(l[fop] as Expr.Op, l[fop - 1], l[fop + 1]) + 
                                     l.slice((fop + 2)..(l.size - 1)))
    }
   
    throw Error("No op in $l")
}

// Kotlin doesn't do mutually recursive peer local functions

fun parseExpr(line: String, precendence: (List<Expr>) -> Expr): Expr {
    var i = 0

    fun parseList(): Expr {

        fun parse(): Expr? {
            if (i >= line.length) return null
                
            val c = line[i]
            ++i

            when (c) {
                '('  -> return parseList()
                ')'  -> return null
                ' '  -> return parse()
                '+'  -> return Expr.Plus()
                '*'  -> return Expr.Times()
                else -> return Expr.Num(c.toString().toLong())
            }
        }

        val exprs = mutableListOf<Expr>()

        while (true) {
            val next = parse()
            if (next == null) return precendence(exprs)
            exprs.add(next)
        }
    }
    

    return parseList()
}

// Answer - 67800526776934
fun processPart1() {
    var sum = 0L
    for (line in data) {
        val parsed = parseExpr(line, ::leftToRightPrecedence)
        val value = parsed.eval()
        println("$value = $line")
        sum += value
    }
    println("Part 1 answer - $sum")
}

fun mulFirstPrecedence(l: List<Expr>): Expr {
    if (l.size == 1) return l[0]

    var fop = -1

    fop = l.indexOfFirst { it is Expr.Plus }
    if (fop < 0) fop = l.indexOfFirst { it is Expr.Times }
    
    if (fop >= 0) {
        return mulFirstPrecedence(l.slice(0..(fop - 2)) + // always empty?
                                  Expr.Call(l[fop] as Expr.Op, l[fop - 1], l[fop + 1]) + 
                                  l.slice((fop + 2)..(l.size - 1)))
    }
   
    throw Error("No op in $l")
}

// Answer - 340789638435483
fun processPart2() {
    var sum = 0L
    for (line in data) {
        val parsed = parseExpr(line, ::mulFirstPrecedence)
        val value = parsed.eval()
        println("$value = $line")
        sum += value
    }
    println("Part 2 answer - $sum")
}

val data = parseLines()
processPart1()
processPart2()

