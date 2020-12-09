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

fun findNonSum(nl: List<Long>, window: Int): Long {

    fun findSum(c: Long, ws: Int, we: Int): Boolean {
	// Upper triangular of nums x nums matrix
	for (i in ws..we) {
	    for (j in (i + 1)..we) {
		val sum = nl[i] + nl[j]
		if (sum == c) return true
	    }
	}
	return false
    }

    for (i in window until nl.size) {
	val candidate = nl[i]
	val isSum = findSum(candidate, i - window, i - 1)
	if (!isSum) return candidate
    }

    throw Error("No non-sums found")
}

// Answer - 1212510616 @ 16 mins
fun processPart1() {
    val result = findNonSum(nums, 25)

    println("Part 1 result: $result")
}

fun findSumSequence(nl: List<Long>, target: Long): Pair<Long, Long> {

    for (i in 0 until nl.size) {
	var partialSum = 0L
	var partialMin = Long.MAX_VALUE
	var partialMax = Long.MIN_VALUE
	for (j in i until nl.size) {
	    partialSum += nl[j]
	    partialMax = maxOf(nl[j], partialMax)
	    partialMin = minOf(nl[j], partialMin)

	    if (partialSum == target) return Pair(partialMin, partialMax)
	    if (partialSum > target) break
	}
    }

    throw Error("No matching sum sequence for $target")
}

// Answer - 171265123 @ 29 mins
fun processPart2() {
    val nonSum = findNonSum(nums, 25)
    val (seqMin, seqMax) = findSumSequence(nums, nonSum)

    println("Part 2 result: $seqMin $seqMax = sum ${seqMin + seqMax}")
}

val data = parseLines()
val nums = data.map({ it.toLong() })
processPart1()
processPart2()
