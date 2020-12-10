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

// Answer - 2272 @ 25 mins
fun processPart1() {
    val js = initialJoltages.toMutableList()
    js.sort()

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
            return search(candidates.drop(1), sofar + cand)
        }

        throw Error("Out of candidates")
    }

    val dj = js.last() + 3
    js.add(dj)
    val path = search(js, listOf(0))

    val diffs = hashMapOf<Int, Int>()
    for (i in 0 until path.size - 1) {
        val diff = path[i + 1] - path[i] 
        diffs[diff] = 1 + (diffs[diff] ?: 0)
    }
    val ones = diffs[1] ?: 0
    val threes = diffs[3] ?: 0
       
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

            
            val cand = candidates.first()
            if (cand - sofar.last() > 3) {
                return
            }

            search(candidates.drop(1), sofar + cand)
            search(candidates.drop(1), sofar)

            return
        }

        search(input.drop(1), listOf(input.first()))

        return sols
    }

    val final = groups.map(::countSolutions).map { it.size.toLong() }.reduce { a, b -> a * b }

    println("Part 2 answer = $final")
}

val data = parseLines()
val initialJoltages = data.map { it.toInt() }

processPart1()
processPart2()
