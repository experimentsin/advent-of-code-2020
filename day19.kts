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

sealed class Rule {

    data class CharRule(val char: Char): Rule()

    data class ListRule(val ruleIndices: List<Int>): Rule()

    data class OrRule(val options: List<Rule>): Rule()
}

fun parseRhs(line: String): Rule {
    val charMatch = destructureWithRegex(line, """\"([a-z])\"""")
    if (charMatch !== null) {
        val (char) = charMatch
        val rule = Rule.CharRule(char[0])
        return rule
    }

    val options = line.split("|")
    if (options.size > 1) {
        val rule = Rule.OrRule(options.map { parseRhs(it.trim()) })
        return rule
    }

    val listMatch = line.split(" ")
    val rl = listMatch.map { it.toInt() }
    val rule = Rule.ListRule(rl)
    return rule
}

fun parseRule(line: String, table: HashMap<Int, Rule>): Rule {

    val charMatch = destructureWithRegex(line, """(\d+)\: (.+)""")
    if (charMatch !== null) {
        val (rulei, rhs) = charMatch
        val rule = parseRhs(rhs)
        table[rulei.toInt()] = rule
        return rule
    }

    throw Error("Bad rule: $line")
}

// Bottom up or top down?
// Top down is surely slower but let's try it anyway

// Returns a list of possible next indices

val NO_MATCHES = listOf<Int>()

fun indent(n: Int) {
    for (i in 1..n) print("  ")
}

fun match1(input: String, index: Int, rule: Rule, depth: Int): List<Int> {
    // indent(depth); println("match1> $input $index $rule")

    val result = match1a(input, index, rule, depth)

    // indent(depth); println("match1< $input $index $rule")

    // indent(depth); println("match1< $result")

    return result
}

fun match1a(input: String, index: Int, rule: Rule, depth: Int): List<Int> {

    if (index >= input.length) return NO_MATCHES

    when (rule) {
        is Rule.CharRule -> {
            if (input[index] == rule.char) {
                return listOf(index + 1)
            } 
        }

        is Rule.ListRule -> {

            fun matchList(subrules: List<Rule>, subindex: Int): List<Int> {
                if (subrules.size == 0) return listOf(subindex)

                val head = subrules.first()
                val tail = subrules.drop(1)

                val headmatches = match1(input, subindex, head, depth + 1)
                if (headmatches.size == 0) return NO_MATCHES

                // indent(depth); println("headmatches ${headmatches}")
 
                val allTailmatches = hashSetOf<Int>()
                for (headmatch in headmatches) {
                    val tailmatches = matchList(tail, headmatch)
                    allTailmatches.addAll(tailmatches)
                }

                // indent(depth); println("alltailmatches ${allTailmatches}")

                return allTailmatches.toList()
            }

            return matchList(rule.ruleIndices.map { ruleMap[it] ?: throw Error("No rule for $it for $rule") }, index)
        }

        is Rule.OrRule -> {

            fun matchOr(subrules: List<Rule>, subindex: Int): List<Int> {
                val allOptmatches = hashSetOf<Int>()
                for (optmatch in subrules) {
                    val optmatches = match1(input, subindex, optmatch, depth + 1)
                    allOptmatches.addAll(optmatches)
                }

                return allOptmatches.toList()
            }

            return matchOr(rule.options, index)
        }
    }

    return NO_MATCHES
}

fun match(input: String): Boolean {
    val options = match1(input, 0, ruleMap[0]!!, 0)
    println("Options $options vs input len ${input.length}")
    return options.size > 0 && options.any { it == input.length }
}

// Answer - 200
fun processPart1() {
    val good = mutableListOf<String>()
    val bad = mutableListOf<String>()
    for (input in inputs) {
        if (match(input)) {
            good.add(input)
        } else {
            bad.add(input)
        }
    }

    println("Good: $good")
    println("Bad: $bad")
    println("Matches: ${good.size}")
}

// Answer - 407
fun processPart2() {
    // Use modified input
}

val dataGroups = parseLineGroups()
val ruleMap = hashMapOf<Int, Rule>()
val rules = dataGroups[0].map { parseRule(it, ruleMap) }
val inputs = dataGroups[1]
processPart1()
processPart2()
