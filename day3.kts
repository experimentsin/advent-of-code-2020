
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

fun parseCell(c: Char): Cell {
    when (c) {
        '.' -> return Cell.EMPTY;
        '#' -> return Cell.TREE;
        else -> throw Error("Unexpected map character $c");
    }
}

data class Location(var x: Int, var y: Int)
data class Slope(var deltax: Int, var deltay: Int)

val map = mutableListOf<List<Cell>>();

// x = 0, y = 0 is top left
fun mapCell(x: Int, y: Int): Cell = map[y][x % mapWidth()];
fun mapCell(l: Location): Cell = mapCell(l.x, l.y);
fun mapWidth() = map[0].size;
fun mapHeight() = map.size;

fun processSlope(slope: Slope): Int {
    val cursor = Location(0, 0);
    var hits = 0;

    do {
        if (mapCell(cursor) == Cell.TREE) ++hits;

        cursor.x += slope.deltax; cursor.y += slope.deltay;
    } while (cursor.y < mapHeight())

    return hits;
}

// Answer: 36
fun processPart1() {
    val hits = processSlope(Slope(3, 2));
    println("Hits $hits");
}

// Answer: 2698900776
fun processPart2() {
    val slopes = listOf(Slope(1, 1), Slope(3, 1), Slope(5, 1), Slope(7, 1), Slope(1, 2));

    var product: Long = 1L;

    for (slope in slopes) {
        val hits = processSlope(slope);
        // println("Hits $hits");
        product *= hits;
    }
    println("Product $product");
}

processInput();

processPart1();
processPart2();
