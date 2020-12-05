fun parseInput(): List<String> {
    val lines = mutableListOf<String>();
    while (true) {
        val line = readLine();
        if (line == null) break;

        lines.add(line);
    }
    return lines;
}

fun binaryPartition(direction: String, index: Int, low: Int, high: Int): Int {
    println("dir $direction $index $low $high")
    if (index >= direction.length) {
        println("Low/high $low $high");
        return low;
    }

    val mid = (low + high) / 2;
    if (isGoLow(direction[index])) {
        return binaryPartition(direction, index + 1, low, mid);
    } else {
        return binaryPartition(direction, index + 1, mid + 1, high);
    }
}

fun isGoLow(c: Char): Boolean {
    return c == 'F' || c == 'L';
}

fun processCardPart1(card: String): Int {
    val fb = card.substring(0, 7);
    val lr = card.substring(7, 10);

    val row = binaryPartition(fb, 0, 0, 127);
    val col = binaryPartition(lr, 0, 0, 7);    

    var id = row * 8 + col;
    println("For $card row $row col $col $id");

    return id;
}

// Answer: 928 @ 23 mins
fun processPart1() {
    var maxId = -1;
    for (card in data) {
        val id = processCardPart1(card);
        maxId = maxOf(id, maxId);
    }
    println("Max $maxId");
}

// Answer: 610 @ 30 mins total
fun processPart2() {
    val ids = data.map(::processCardPart1).toMutableList();
    ids.sort();

    var last = -1;
    for (id in ids) {
        if (last >= 0 && last + 1 != id) {
            println("Missing: ${id - 1}");
        }
        last = id;
    }
    println("sorted: $ids")
}

val data = parseInput();
processPart1();
processPart2();