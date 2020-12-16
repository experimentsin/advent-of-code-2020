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

// Answer - 26053
fun processPart1() {
    val invalid = mutableListOf<List<Int>>()
    val invvals = mutableListOf<Int>()
    for (t in nearbyTickets) {
        for (fv in t) {
            if (rules.values.all { rule -> !rule.isValid(fv) }) {
                invalid.add(t)
                invvals.add(fv)
            }
            
        }
    }
    println("Part 1 answer - invalid values sum ${invvals.sum()}")
}

// Answer - 1515506256421
fun processPart2() {

    // Throw away obviously invalid options to simplify
    
    val invalid = mutableListOf<List<Int>>()
    for (t in nearbyTickets) {
        for (fv in t) {
            if (rules.values.all { rule -> !rule.isValid(fv) }) {
                invalid.add(t)
                break
            }
        }
    }
    val valid = nearbyTickets - invalid

    val candidates = mutableListOf<HashSet<Rule>>()
    for (i in valid.first().indices) {
        val irules = rules.values.toHashSet()
        for (t in valid) {
            val ivalue = t[i]
            for (rule in rules.values) {
                if (!rule.isValid(ivalue)) {
                    irules.remove(rule)
                }
            }
        }
        candidates.add(irules)
    }

    val counts = candidates.map { it.size }
    println("Counts ${counts}")

    // Then try brute force with memoisation

    // We could shave off a constant multiplier by memoising on something simpler than
    // the whole rule structure (e.g. rule index or rule field name) but this happens to be fast
    // enough for this particular problem
    //
    // Note that the sol parameter is a partial result accumulator and doesn't need to play in
    // the memoisation

    val nosol = hashSetOf<Pair<Int, Set<Rule>>>()
    val sols = mutableListOf<List<Rule>>()

    fun search(used: Set<Rule>, i: Int, sol: List<Rule>): Boolean {
        val memokey = Pair(i, used)
        if (memokey in nosol) return false

        if (i >= candidates.size) {
            // We could just stop right here since we're only interested in the first solution,
            // which is implied to be unique
            sols.add(sol)
            return true
        }

        val icands = candidates[i] - used
        var solfound = false
        for (icand in icands) {
            if (search(used + icand, i + 1, sol + icand)) solfound = true
        }

        if (!solfound) nosol.add(memokey)
        return solfound
    }

    search(hashSetOf<Rule>(), 0, listOf<Rule>())

    println("Sol count ${sols.size}")

    val sol = sols[0]

    var departures = 1L
    for ((rulei, rule) in sol.withIndex()) {
        if (rule.field.startsWith("departure")) {
            val value = yourTicket[rulei]
            println("${rule.field} = $value")
            departures *= value
        }
    }
    println("Part 2 answer - departures prod ${departures}")
    
}

data class Rule(val field: String, val r1: IntRange, val r2: IntRange)

fun Rule.isValid(value: Int) = value in r1 || value in r2

fun parseRules(lines: List<String>): HashMap<String, Rule> {
    val rules = hashMapOf<String, Rule>()
    for (line in lines) {
        val (field, r1lo, r1hi, r2lo, r2hi)
            = destructureWithRegex(line, """([a-z ]+)\: (\d+)\-(\d+) or (\d+)\-(\d+)""") ?: throw Error("Bad line $line")
        val rule = Rule(field, r1lo.toInt()..r1hi.toInt(), r2lo.toInt()..r2hi.toInt())
        rules[rule.field] = rule
    }
    return rules
}

fun parseTickets(lines: List<String>): List<List<Int>> {
    val tickets = mutableListOf<List<Int>>()
    for (line in lines) {
        tickets.add(line.split(",").map { it.toInt() })
    }
    return tickets
}

val data = parseLineGroups()
val rules = parseRules(data[0])
// println("-- ${rules.size} $rules")

val yourTicket = parseTickets(data[1].drop(1))[0]
// println("-- ${yourTicket}")

val nearbyTickets = parseTickets(data[2].drop(1))
// println("-- ${nearbyTickets.size} ${nearbyTickets}")


processPart1()
processPart2()
