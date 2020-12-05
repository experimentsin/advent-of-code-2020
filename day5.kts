fun parseInput(): List<String> {
    val lines = mutableListOf<String>()
    while (true) {
        val line = readLine()
        if (line == null) break

        lines.add(line)
    }
    return lines
}

fun binaryPartition(codes: String, isLowCode: (Char) -> Boolean, index: Int, low: Int, high: Int): Int {
    // println("dir $codes $index $low $high")
    if (index >= codes.length) {
        // println("Low/high $low $high")
        return low
    }

    val mid = (low + high) / 2
    if (isLowCode(codes[index])) {
        return binaryPartition(codes, isLowCode, index + 1, low, mid)
    } else {
        return binaryPartition(codes, isLowCode, index + 1, mid + 1, high)
    }
}

fun processCardPart1(card: String): Int {
    val fb = card.substring(0, 7)
    val lr = card.substring(7, 10)

    val row = binaryPartition(fb, { it == 'F' }, 0, 0, 127)
    val col = binaryPartition(lr, { it == 'L' }, 0, 0, 7)

    var id = row * 8 + col
    // println("For $card row $row col $col $id")

    return id
}

// Answer: 928 @ 23 mins
fun processPart1() {
    val maxId = data.map(::processCardPart1).maxOrNull()
    println("Max $maxId")
}

// Answer: 610 @ 30 mins total
fun processPart2() {
    val ids = data.map(::processCardPart1).toMutableList()
    ids.sort();
    val range = ids.first()..ids.last()
    val diff = range.subtract(ids)
    println("diff $diff")
}

val data = parseInput()
processPart1()
processPart2()