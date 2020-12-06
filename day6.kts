fun parseInput(): List<List<String>> {
    val forms = mutableListOf<List<String>>()

    var formLines = mutableListOf<String>()

    fun commitForm() {
        if (!formLines.isEmpty()) {
            forms.add(formLines)
            formLines = mutableListOf<String>()
        }
    }

    while (true) {
        val line = readLine()

        // End of file
        if (line == null) {
            commitForm()
            return forms
        }

        val trimmed = line.trim()

        // Blank line
        if (trimmed.isEmpty()) {
            commitForm()
            continue
        }

        // Line with content
        formLines.add(line.trim())
    }
}

// Answer: 6297 @ 10 mins
fun processPart1() {
    var total = 0
    for (group in data) {
	val yesSet = mutableSetOf<Char>()
	for (person in group) {
	    for (c in person) {
		yesSet.add(c)
	    }
	}
	// println("$group = $yesSet = ${yesSet.size}");
	total += yesSet.size
    }
    println("Total $total")
}

// Answer: 3158 @ 19 mins
fun processPart2() {
    var total = 0
    for (group in data) {
	var allSet: Set<Char>? = null
	for (person in group) {
	    if (allSet === null) {
		allSet = person.toHashSet()
		continue
	    }

	    allSet = allSet.intersect(person.toHashSet())
	}
	// println("$group = $allSet = ${allSet!!.size}");
	total += allSet!!.size
    }
    println("Total $total")
}

val data = parseInput()
processPart1()
processPart2()
