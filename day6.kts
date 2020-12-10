fun parseInput(): List<List<String>> {
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

        // End of file
        if (line == null) {
            commitGroup()
            return groups
        }

        val trimmed = line.trim()

        // Blank line
        if (trimmed.isEmpty()) {
            commitGroup()
            continue
        }

        // Line with content
        groupLines.add(line.trim())
    }
}

// Notes: Switched over to using Emacs with Kotlin mode at this point
    
// Answer: 6297 @ 10 mins
fun processPart1() {
    var total = 0
    for (group in data) {
        var yesSet = mutableSetOf<Char>()
        for (person in group) {
            yesSet.addAll(person.toList())
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

            allSet = allSet intersect person.toHashSet()
        }
        // println("$group = $allSet = ${allSet!!.size}");
        total += allSet!!.size
    }

    // Or if you want to sound precocious 
    // val total = data.sumBy({ it.map({ it.toSet() }).reduce({ s1, s2 -> s1 intersect s2 }).size })

    println("Total $total")
}

val data = parseInput()
processPart1()
processPart2()
