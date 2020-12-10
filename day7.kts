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

data class Colour(val adjective: String, val hue: String)
data class NColour(val n: Int, val colour: Colour)
data class Rule(val colour: Colour, val canContain: MutableList<NColour> = mutableListOf<NColour>())

fun parseLine2(line: String): Rule {
    val (adj, col, spec) = destructureWithRegex(line, """([a-z]+) ([a-z]+) bags contain (.*)\.""") ?: throw Error("No match for $line")
    // println("$adj $col -- $spec")

    val rule = Rule(Colour(adj, col))

    if (spec == "no other bags") {
        // println("no other")
        return rule
    }

    val more = spec.split(",")
    for (colspec in more) {
        val (nmore, adjmore, colmore) = destructureWithRegex(colspec.trim(), """(\d+) ([a-z]+) ([a-z]+) bags?""") ?: throw Error("No match for $colspec")
        // println("$nmore $adjmore $colmore")

        rule.canContain.add(NColour(nmore.toInt(), Colour(adjmore, colmore)))
    }

    return rule
}

fun countContainers(rules: List<Rule>, target: Colour): Int {
    val seen = hashSetOf<Colour>()

    fun search(c: Colour) {
        if (c in seen) return
        seen.add(c)
        
        val possibleContainers
            = rules.filter { rule -> c in rule.canContain.map { it.colour } }
                   .map { rule -> rule.colour }

        possibleContainers.forEach(::search)
    }

    search(target)

    return seen.size - 1 // because we don't count the target
}

// Answer - 259 @ 55mins
fun processPart1() {
    val target = Colour("shiny", "gold")
    val result = countContainers(rules, target)

    println("Result: $result")
}

fun countContained(rules: List<Rule>, target: Colour): Int {

    fun search(c: Colour): Int {
        var total = 1
        val crule = rules.find({ c == it.colour })!!
        for (contained in crule.canContain) {
            val containedn = contained.n
            val allContained = search(contained.colour)
            total += containedn * allContained
        }
        return total
    }

    return search(target) - 1 // because we don't count the target
}

// Answer - 45018 @ 1h 4mins
fun processPart2() {
    val target = Colour("shiny", "gold")
    val result = countContained(rules, target)

    println("Result: $result")
}

val data = parseLines()
val rules = data.map(::parseLine2)
processPart1()
processPart2()
