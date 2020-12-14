/// Boilerplate

fun parseLines(): List<String> {
    var lines = mutableListOf<String>()

    while (true) {
        val line = readLine()
        if (line == null) return lines
        lines.add(line.trim())
    }
}

fun <T>parseLinesAsGridAndMap(mapper: (c: Char) -> T): List<List<T>> {
    return parseLines().map({ it.map(mapper).toMutableList() })
}

/// Solution

data class Vec2(val x: Int, val y: Int)

// In (x, y) format, y increases downwards
val ADJ = listOf(Vec2(-1, -1), Vec2(0, -1), Vec2(1, -1),
                 Vec2(-1, 0),               Vec2(1, 0),
                 Vec2(-1, 1),  Vec2(0, 1) , Vec2(1, 1))

fun addVec2(v1: Vec2, v2: Vec2) = Vec2(v1.x + v2.x, v1.y + v2.y)

fun iterate(g: Grid, finder: (Grid, Vec2) -> List<Cell>, occThreshold: Int): Pair<Grid, Int> {
    val output = copyGrid(g)
    var changes = 0

    for (y in g.indices) {
        for (x in g[0].indices) {
            val adj = finder(g, Vec2(x, y))
            // println("Adj $x $y - $adj")

            if (g[y][x] == Cell.EMPTY) {
                if (adj.count { it == Cell.OCCUPIED } == 0) {
                    output[y][x] = Cell.OCCUPIED
                    ++changes
                }
                continue
            }

            if (g[y][x] == Cell.OCCUPIED) {
                if (adj.count { it == Cell.OCCUPIED } >= occThreshold) {
                    output[y][x] = Cell.EMPTY
                    ++changes
                }
                continue
            }
        }
    }

    return Pair(output, changes)
}

fun adjacentSeats(g: Grid, o: Vec2): List<Cell> {
    return ADJ.mapNotNull(
        fun (dv: Vec2): Cell? {
            val adjv = addVec2(o, dv)
            if (isGridOob(g, adjv)) return null

            val cell = g[adjv.y][adjv.x]
            if (cell == Cell.FLOOR) return null

            return cell
        }
    )
}

// Answer - 2273 @ 46mins
fun processPart1() {
    // println("Grid: $initialGrid")

    var currentg = initialGrid

    while (true) {
        // draw(currentg)
        // println("--")
        val (nextg, changes) = iterate(currentg, ::adjacentSeats, 4)
        if (changes == 0) break
        currentg = nextg
    }

    println("")
    draw(currentg)

    val occ = countGrid(currentg, { it == Cell.OCCUPIED })
    println("Part 1 occupied: $occ")
}

fun visibleSeats(g: Grid, o: Vec2): List<Cell> {
    return ADJ.mapNotNull(
        fun (dv: Vec2): Cell? {
            var cursor = o 

            while (true) {
                cursor = addVec2(cursor, dv)
                if (isGridOob(g, cursor)) break

                val cell = g[cursor.y][cursor.x]
                if (cell != Cell.FLOOR) {
                    return cell
                }
            }

            return null
        }
    )
}

// Answer - 2064 @ 1h 6mins
fun processPart2() {
    // println("Grid: $initialGrid")

    var currentg = initialGrid

    while (true) {
        // draw(currentg)
        // println("--")
        val (nextg, changes) = iterate(currentg, ::visibleSeats, 5)
        if (changes == 0) break
        currentg = nextg
    }

    println("")
    draw(currentg)

    val occ = countGrid(currentg, { it == Cell.OCCUPIED })
    println("Part 2 occupied: $occ")
}

fun draw(g: Grid) {
    for (row in g) {
        for (cell in row) {
            print(cell.display)
        }
        println("")
    }
}

enum class Cell(val display: Char) { FLOOR('.'), EMPTY('L'), OCCUPIED('#') }

fun parseCell(c: Char) = Cell.values().find { it.display == c } ?: throw Error("Bad grid input $c")

typealias Grid = List<List<Cell>>
typealias MutableGrid = MutableList<MutableList<Cell>>

fun gridWidth(g: Grid) = g[0].size
fun gridHeight(g: Grid) = g.size
fun isGridOob(g: Grid, v: Vec2) = if ((v.x < 0 || v.x >= gridWidth(g)) || (v.y < 0 || v.y >= gridHeight(g))) true else false

fun gridCell(g: Grid, v: Vec2) = g[v.y][v.x]
fun setGridCell(g: MutableGrid, v: Vec2, value: Cell) {
    g[v.y][v.x] = value
}

fun toMutableGrid(g: List<List<Cell>>) = g.map { it.toMutableList() }.toMutableList()
fun copyGrid(g: Grid): MutableList<MutableList<Cell>> = toMutableGrid(g)
fun countGrid(g: Grid, counter: (Cell) -> Boolean) = g.sumBy { it.count(counter) }

val initialGrid = parseLinesAsGridAndMap(::parseCell)
processPart1()
processPart2()
