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

fun perms(ialls: List<String>, iings: List<String>): Set<HashMap<String, String>> {
    val perms = hashSetOf<HashMap<String, String>>()

    fun walk(alls: List<String>, ings: List<String>, assigns: Set<Assign>) {
        if (alls.size == 0) {
            // Everything's been assigned
            perms.add(setToMap(assigns))
            return
        }

        val headall = alls[0]
        val tailalls = alls.drop(1)
        
        for (ing in ings) {
            if (ing !in filteredAllMap[headall]!!) continue
            walk(tailalls, ings - ing, assigns + Assign(headall, ing))
        }
    }

    walk(ialls, iings, hashSetOf<Assign>())

    return perms
}

fun perms(food: Food): Set<HashMap<String, String>> {
    return perms(food.alls, food.ings)
}

// We return null is there's a contradiction between the two all->ing assignment maps
fun merge(ass1: Map<String, String>, ass2: Map<String, String>): HashMap<String, String>? {
    val merged = HashMap(ass1)

    for ((k2, v2) in ass2) {
        val v1 = ass1[k2]
        if (v1 !== null && v1 != v2) return null
        merged[k2] = v2
    }

    return merged
}

fun solve1(): Pair<Set<String>, List<HashMap<String, String>>> {
    val nonAllCandidates = allIngs.toHashSet()
    val sols = mutableListOf<HashMap<String, String>>()

    fun walk(foods: List<Food>, allAssigns: HashMap<String, String>) {
        if (foods.size == 0) {
            // Here, we have a non-contradictory set of assignments
            // That means everything on the rhs of an assignments *could* be an allergen
            // So let's remove them from the candidate set
            sols.add(allAssigns)

            // And remember this allergen to ingredient mapping solution
            nonAllCandidates.removeAll(allAssigns.values.toList())
            return
        }

        // Try all possible assignments
        val food = foods[0]
        val foodstail = foods.drop(1)

        // We could send allAssigns into the perm generator rather than over-generate
        // and merge afterwards but it's fast enough as it is
        val assignperms = perms(food)

        for (ass in assignperms) {
            val merged = merge(ass, allAssigns)
            if (merged == null) continue
            
            walk(foodstail, merged)
        }
    }

    walk(foods, hashMapOf<String, String>())

    return Pair(nonAllCandidates, sols)
}

val filteredAllMap = hashMapOf<String, Set<String>>()

fun prefilter() {
    for (all in allAlls) {
        var possibles = allIngs.toSet()
        for (food in foods) {
            if (all in food.alls) {
                possibles = possibles intersect food.ings
            }
        }
        filteredAllMap[all] = possibles
    }
}

// Answer 1 - 2724
// Answer 2 - xlxknk,cskbmx,cjdmk,bmhn,jrmr,tzxcmr,fmgxh,fxzh
fun processPart1and2() {
    println("Foods: ${foods.size}")
    println("Ingredients ${allIngs.size}")
    println("Allergens: ${allAlls}")

    prefilter()

    // println("perms ${perms(foods[0].alls, foods[0].ings)}")

    val (nonAlls, sols) = solve1()
    println("Non allergens: ${nonAlls}")

    var count = 0
    nonAlls.forEach { ing -> count += foods.count { ing in it.ings } }
    println("Part 1 answer - $count is the number of times $nonAlls appear")

    // It seems there is actually on one solution for the input data
    val sol = sols[0]
    val sortedAlls = sol.keys.toList().sorted()
    val ans = sortedAlls.map { sol[it] }.joinToString(",")
    println("Part 2 answer - $ans are the ingredients sorted by allergen for solution $sol")
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

processPart1and2()

