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

// I'm only nesting these classes to work around a bug in the kts compiler

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

// Kotlin doesn't do mutually recursive peer local functions so we have
// to nest them here. D'oh!

// Quick and dirty hack parser e.g. lists with no closing brace get
// auto-closed by EOL

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
                // All test data seems to use just single digit numbers so
                in "0123456789" -> return Expr.Num(c.toString().toLong())
                else -> throw Error("Unexpected $c at $i in $line")
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

fun leftToRightPrecedence(l: List<Expr>): Expr {
    if (l.size == 1) return l.first()

    val fop = l.indexOfFirst { it is Expr.Op }
    if (fop >= 0) {
        return leftToRightPrecedence(l.slice(0..(fop - 2)) + // Should always be empty in L to R
                                     Expr.Call(l[fop] as Expr.Op, l[fop - 1], l[fop + 1]) + 
                                     l.slice((fop + 2)..(l.size - 1)))
    }
   
    throw Error("No op in $l")
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

    var fop: Int

    fop = l.indexOfFirst { it is Expr.Plus }
    if (fop < 0) fop = l.indexOfFirst { it is Expr.Times }
    
    if (fop >= 0) {
        return mulFirstPrecedence(l.slice(0..(fop - 2)) + 
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

