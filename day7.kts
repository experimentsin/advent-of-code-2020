import kotlin.math.*

fun parseLines(): List<String> {
    var lines = mutableListOf<String>()

    while (true) {
        val line = readLine()
        if (line == null) return lines
        lines.add(line.trim())
    }
}

data class Colour(val adjective: String, val hue: String)
data class NColour(val n: Int, val colour: Colour)
data class Rule(val colour: Colour, val canContain: MutableList<NColour> = mutableListOf<NColour>())

fun parseLine2(line: String): Rule {
    val case1 = """([a-z]+) ([a-z]+) bags contain (.*)\.""".toRegex()
    val match1 = case1.matchEntire(line)
    if (match1 === null) {
	println("No match for $line")
	throw Error("Rats")
    }
    val (adj, col, spec) = match1.destructured
    // println("$adj $col -- $spec")

    val rule = Rule(Colour(adj, col))

    if (spec == "no other bags") {
	// println("no other")
	return rule
    }

    

    val more = spec.split(",")
    val moreCase = """(\d+) ([a-z]+) ([a-z]+) bags?""".toRegex()
    for (colspec in more) {
	val trimmed = colspec.trim()
	val moreMatch = moreCase.matchEntire(trimmed)
	if (moreMatch === null) {
	    println("No match for $colspec")
	    throw Error("Rats")
	}
	val (nmore, adjmore, colmore) = moreMatch.destructured
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
	
	val possibleContainers = rules.filter({ c in it.canContain.map({ it.colour }) }).map({ it.colour })

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
	val crule = rules.filter({ c == it.colour })[0]
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
