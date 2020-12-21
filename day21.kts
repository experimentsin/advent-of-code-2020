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

/*

Each allergen is found in exactly one ingredient. Each ingredient
contains zero or one allergen. Allergens aren't always marked; when
they're listed (as in (contains nuts, shellfish) after an ingredients
list), the ingredient that contains each listed allergen will be
somewhere in the corresponding ingredients list. However, even if an
allergen isn't listed, the ingredient that contains that allergen
could still be present: maybe they forgot to label it, or maybe it was
labeled in a language you don't know.

 */

data class Assign(val all: String, val ing: String)

fun setToMap(s: Set<Assign>): HashMap<String, String> {
    val m = hashMapOf<String, String>()
    s.forEach { m[it.all] = it.ing }
    return m
}

// Can we find a contradiction

fun perms(il1: List<String>, il2: List<String>): Set<HashMap<String, String>> {
    val perms = hashSetOf<HashMap<String, String>>()

    fun walk(l1: List<String>, l2: List<String>, assigns: Set<Assign>) {
        if (l1.size == 0) {
            // Everything's been assigned
            perms.add(setToMap(assigns))
            return
        }

        val head1 = l1[0]
        val tail1 = l1.drop(1)
        
        for (e2 in l2) {
            walk(tail1, l2 - e2, assigns + Assign(head1, e2))
        }
    }

    walk(il1, il2, hashSetOf<Assign>())

    return perms
}

val CACHE = HashMap<Int, Set<HashMap<String, String>>>()
fun perms(food: Food): Set<HashMap<String, String>> {
    val cached = CACHE[food.id]
    if (cached !== null) return cached

    val pms = perms(food.alls, food.ings)
    CACHE[food.id] = pms
    return pms
}

// In all these assignment pairs, it's (all, ing)

fun isContradictory(ass1: Set<Assign>, ass2: Set<Assign>): Boolean {
    for (a1 in ass1) {
        for (a2 in ass2) {
            if (a1.all == a2.all && a1.ing != a2.ing) return true
        }
    }

    return false
}

fun merge(ass1: Map<String, String>, ass2: Map<String, String>): HashMap<String, String>? {
    val merged = HashMap(ass1)

    for ((k2, v2) in ass2) {
        val v1 = ass1[k2]
        if (v1 !== null && v1 != v2) return null
        merged[k2] = v2
    }

    return merged
}

fun solve1(): Set<String> {
    val nonAllCandidates = allIngs.toHashSet()

    fun walk(foods: List<Food>, allassigns: HashMap<String, String>) {
        // println("walk ${foods.size} ${allassigns.size}")

        if (foods.size == 0) {
            // Here, we have a non-contradictory set of assignments
            // That means everything on the rhs of an assignments *could* be an allergen
            // So let's remove them from the candidate set
            nonAllCandidates.removeAll(allassigns.values.toList())
            return
        }

        // Try all possible assignments
        val food = foods[0]
        val foodtail = foods.drop(1)
        // val assignperms = perms(food.alls, food.ings)
        val assignperms = perms(food)

        for (ass in assignperms) {
            /*
            if (isContradictory(ass, allassigns)) {
                continue
            }
             */
            val merged = merge(ass, allassigns)
            if (merged == null) continue
            
            walk(foodtail, merged)
        }
    }

    walk(foods, hashMapOf<String, String>())

    return nonAllCandidates
}

fun processPart1() {
    println("foods ${foods.size}")
    println("ings ${allIngs.size}")
    println("alls ${allAlls}")

    // println("perms ${perms(foods[0].alls, foods[0].ings)}")

    val nonalls = solve1()
    println("nonalls ${nonalls}")

    var count = 0
    nonalls.forEach { ing -> count += foods.count { ing in it.ings } }
    println("Sol $count")
}

fun processPart2() {
}

data class Food(val ings: List<String>, val alls: List<String>, val id: Int)

var foodId = 0
fun parseFood(line: String): Food {
    val (ings, alls) = destructureWithRegex(line, """(.*) \(contains (.*)\)""") ?: throw Error("Bad line $line")
    return Food(ings.split(" "), alls.split(", "), foodId++)
}

val input = parseLines()
val foods = input.map(::parseFood)

val allIngs = foods.flatMap { it.ings }.distinct()
val allAlls = foods.flatMap { it.alls }.distinct()

processPart1()
processPart2()

