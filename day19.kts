/// Boilerplate

import kotlin.math.*

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

sealed class Rule {
    data class CharRule(val char: Char): Rule()
    data class ListRule(val ruleIndices: List<Int>): Rule()
    data class OrRule(val options: List<Rule>): Rule()
}

// Each match function returns a list of next indices corresponding
// so how much every possible match consumes

val NO_MATCHES = listOf<Int>()

fun match1(input: String, index: Int, rule: Rule, depth: Int): List<Int> {

    if (index >= input.length) return NO_MATCHES

    when (rule) {

        is Rule.CharRule -> {
            if (input[index] == rule.char) {
                return listOf(index + 1)
            } else {
                return NO_MATCHES
            }
        }

        is Rule.ListRule -> {

            fun matchList(subrules: List<Rule>, subindex: Int): List<Int> {
                if (subrules.size == 0) return listOf(subindex)

                val head = subrules.first()
                val tail = subrules.drop(1)

                val headmatches = match1(input, subindex, head, depth + 1)
                if (headmatches.size == 0) return NO_MATCHES

                val allTailmatches = hashSetOf<Int>()
                for (headmatch in headmatches) {
                    val tailmatches = matchList(tail, headmatch)
                    allTailmatches.addAll(tailmatches)
                }

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

}

fun match(input: String): Boolean {
    val options = match1(input, 0, ruleMap[0]!!, 0)
    // println("Options $options vs input len ${input.length} of $input")
    return options.any { it == input.length }
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
    // println("Bad: $bad")
    println("Part 1 answer - ${good.size} matches")
}

// Answer - 407
fun processPart2() {
    // Apply rule patches
    parseRule("8: 42 | 42 8", ruleMap)
    parseRule("11: 42 31 | 42 11 31", ruleMap)
    
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
    // println("Bad: $bad")
    println("Part 2 answer - ${good.size} matches")
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
    val (rulei, rhs) = destructureWithRegex(line, """(\d+)\: (.+)""") ?: throw Error("Bad rule: $line")
    val rule = parseRhs(rhs)
    table[rulei.toInt()] = rule

    return rule
}

val dataGroups = parseLineGroups()
val ruleMap = hashMapOf<Int, Rule>()
val rules = dataGroups[0].map { parseRule(it, ruleMap) }
val inputs = dataGroups[1]

processPart1()
processPart2()
