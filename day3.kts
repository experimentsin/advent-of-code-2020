
fun processInput() {
    while (true) {
        val line = readLine();
        if (line == null) break;

        val mapLine = line.map(::parseCell);
        map.add(mapLine);

        // println("Parsed: $mapLine");
    }
}

enum class Cell {
    EMPTY,
    TREE
}

data class Location(var x: Int, var y: Int)
data class Slope(var deltax: Int, var deltay: Int)


val map = mutableListOf<List<Cell>>();

// x = 0, y = 0 is top left
fun mapCell(x: Int, y: Int): Cell = map[y][x % map[0].size];
fun mapCell(l: Location): Cell = mapCell(l.x, l.y);

fun parseCell(c: Char): Cell {
    when (c) {
        '.' -> return Cell.EMPTY;
        '#' -> return Cell.TREE;
        else -> throw Error("Unexpected map character $c");
    }
}

fun processSlope(deltax: Int, deltay: Int): Int {
    val cursor = Location(0, 0);
    var treeHits = 0;

    while (true) {
        if (mapCell(cursor) == Cell.TREE) ++treeHits;
        cursor.x += deltax;
        cursor.y += deltay;

        if (cursor.y >= map.size) break;
    }

    return treeHits;
}

// Answer: 36
fun processPart1() {
    val hits = processSlope(3, 2);
    println("Hits $hits");
}

// Answer: 2698900776
fun processPart2() {
    val slopes = listOf(Slope(1, 1), Slope(3, 1), Slope(5, 1), Slope(7, 1), Slope(1, 2));

    var product: Long = 1L;

    for (slope in slopes) {
        val hits = processSlope(slope.deltax, slope.deltay);
        // println("Hits $hits");
        product *= hits;
    }
    println("Product $product");
}

processInput();

processPart1();
processPart2();
