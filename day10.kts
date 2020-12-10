/// Boilerplate

fun parseLines(): List<String> {
    var lines = mutableListOf<String>()

    while (true) {
        val line = readLine()
        if (line == null) return lines
        lines.add(line.trim())
    }
}

/// Solution

fun <T>extendList(l: List<T>, e: T): List<T> {
    val copy = l.toMutableList()
    copy.add(e)
    return copy
}

fun <T>trimList(l: List<T>, e: T): List<T> {
    val copy = l.toMutableList()
    copy.remove(e)
    return copy
}

// Answer - 2272 @ 25 mins
fun processPart1() {
    val js = initialJoltages.toMutableList()
    js.sort()
    val dj = js.last() + 3

    fun search(candidates: List<Int>, sofar: List<Int>): List<Int>  {
        // println("search $candidates $sofar")
        if (candidates.size == 0) {
            return sofar
        }

        for (i in candidates.indices) {
            val cand = candidates[i]
            if (cand - sofar.last() > 3) {
                throw Error("Impossible at $cand $i")
            }
            return search(trimList(candidates, cand), extendList(sofar, cand))
        }

        throw Error("Out of candidates")
    }

    js.add(dj)
    val path = search(js, listOf(0))

    val diffs = mutableListOf<Int>()
    for (i in 0 until path.size - 1) {
        diffs.add(path[i + 1] - path[i])
    }
    val ones = diffs.count { it == 1 }
    val threes = diffs.count { it == 3 }
       
    println("Part 1 answer: $ones $threes, prod = ${ones * threes}")
}

// Answer - 84627647627264

fun processPart2() {
    val js = initialJoltages.toMutableList()
    js.sort()
    val dj = js.last() + 3

    js.add(dj)
    js.add(0, 0)

    println("js $js")

    val groups = mutableListOf<List<Int>>()
    var starti = 0
    for (i in 0 until js.size - 1) {
        if (i + 1 == js.size - 1) {
            groups.add(js.slice(starti..js.size - 1))
            break
        }

        if (js[i + 1] - js[i] == 3) {
            groups.add(js.slice(starti..i))
            starti = i + 1
            continue
        }
    }

    // println("Groups $groups")

    fun countSolutions(input: List<Int>): List<List<Int>> {
        if (input.size == 1) return listOf(input)
        
        val sols = mutableListOf<List<Int>>()

        fun search(candidates: List<Int>, sofar: List<Int>) {
            // println("search $candidates $sofar")
            if (candidates.size == 0) {
                if (sofar.last() == input.last()) {
                    sols.add(sofar)
                }
                return
            }

            
            val cand = candidates[0]
            if (cand - sofar.last() > 3) {
                return
            }

            search(trimList(candidates, cand), extendList(sofar, cand))
            search(trimList(candidates, cand), sofar)

            return
        }

        search(trimList(input, input[0]), listOf(input[0]))

        return sols
    }

    var final = 1L
    for (g in groups) {
        val sols = countSolutions(g)
        println("${sols.size} for $g - $sols")
        final *= sols.size
    }

    println("Part 2 answer = $final")
}

val data = parseLines()
val initialJoltages = data.map { it.toInt() }

processPart1()
processPart2()
