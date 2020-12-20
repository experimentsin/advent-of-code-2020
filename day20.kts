/// Boilerplate

import kotlin.math.*

fun parseLineGroups(): List<List<String>> {
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

        if (line == null) {
            commitGroup()
            return groups
        }

        val trimmed = line.trim()

        if (trimmed.isEmpty()) {
            commitGroup()
            continue
        }

        groupLines.add(line.trim())
    }
}

val REGEX_CACHE = hashMapOf<String, Regex>()
fun destructureWithRegex(input: String, pattern: String): MatchResult.Destructured? {
    var regex = REGEX_CACHE[pattern]
    if (regex === null) {
	regex = Regex(pattern)
	REGEX_CACHE[pattern] = regex
    }

    val matchResult = regex.matchEntire(input)
    if (matchResult === null) return null
    return matchResult.destructured
}

/// Solution

typealias Tile = List<List<Char>>

fun drawTile(t: Tile) {
    t.forEach { println(it.joinToString("")) }
}

fun flipV(t: Tile): Tile {
    return t.reversed()
}

fun flipH(t: Tile): Tile {
    return t.map { it.reversed() }
}

fun flips(t: Tile): List<Tile> {
    return listOf(t, flipV(t), flipH(t), flipV(flipH(t)), flipH(flipV(t)))
}

fun rotateL(t: Tile): Tile {
    val rotated = t[0].indices.map { t.indices.map { '?' } }.toMutableList()

    for (rotatedi in rotated.indices) {
        rotated[rotatedi] = t.map { it[it.size - rotatedi - 1] }
    }

    return rotated
}

fun rotations(t: Tile): List<Tile> {
    val rots = mutableListOf<Tile>()

    var rot = t
    rots.add(rot)

    for (i in 1..3) {
        rot = rotateL(t)
        rots.add(rot)
    }

    return rots
}

fun allTransformations(t: Tile): List<Tile> {
    val all = rotations(t).flatMap { flips(it) }
    return all.distinct()
}

val tileSymmetries = hashMapOf<Int, List<Tile>>()

fun vertEdgeMatch(tabove: Tile, tbelow: Tile): Boolean {
    return tabove.last() == tbelow.first()
}

fun horizEdgeMatch(tleft: Tile, tright: Tile): Boolean {
    return tleft.map { it.last() } == tright.map { it.first() }
}

fun solveGrid(): List<Pair<List<Int>, List<Tile>>> {

    val sols = mutableListOf<Pair<List<Int>, List<Tile>>>()

    fun isBorderMatch(tsofar: List<Tile>, sym: Tile): Boolean {
        if (tsofar.size == 0) return true

        val symr = tsofar.size / squareDim
        val symc = tsofar.size % squareDim

        for ((i, t) in tsofar.withIndex()) {
            val ir = i / squareDim
            val ic = i % squareDim

            if (symr == ir && symc == ic + 1) {
                // Same row, sym to the right of t
                if (!horizEdgeMatch(t, sym)) return false
            }

            if (symc == ic && symr == ir + 1) {
                // Same colum, sym beneath t
                if (!vertEdgeMatch(t, sym)) return false
            }
        }

        return true
    }

    fun walk(options: List<Int>, idsofar: List<Int>, tsofar: List<Tile>) {

        if (options.size == 0) {
            sols.add(Pair(idsofar, tsofar))
        }

        for (opt in options) {
            val syms = tileSymmetries[opt]!!
            for (sym: Tile in syms) {
                val borderMatch = isBorderMatch(tsofar, sym)
                if (borderMatch) {
                    val extended = tsofar.toMutableList()
                    extended.add(sym)
                    walk(options - opt, idsofar + opt, extended)
                }
            }
        }
    }

    walk(tileMap.keys.toList(), listOf<Int>(), listOf<Tile>())

    return sols
}

var solutionTiles = listOf<Tile>()

// Answer - 23497974998093
fun processPart1() {
    println("Tile count = ${tileMap.size}, Grid dim = ${squareDim}, Tile dim = ${tileDim}")

    for ((id, t) in tileMap) {
        tileSymmetries[id] = allTransformations(t)
    }

    // We've redundantly computed every flipped/rotated equivalent solution but it was
    // quick enough so it doesn't matter; just pick one
    val sols = solveGrid()
    val sol = sols[0]

    val ids = sol.first
    val corners = listOf(
        ids[0], ids[squareDim - 1],
        ids[squareDim * squareDim - squareDim], ids[squareDim * squareDim - 1]
    )

    val cornerProd = corners.fold(1L) { acc, v -> acc * v }

    println("Part 1 answer - ${cornerProd}, product of corners ${corners}")

    solutionTiles = sol.second
}

fun renderBorderlessGrid(tiles: List<Tile>): Tile {
    val borderlessTileDim = tileDim - 2
    val targetdim = squareDim * borderlessTileDim
    val target = (1..targetdim).map { (1..targetdim).map { '?' }.toMutableList() }.toMutableList()
    
    for ((i, tile) in tiles.withIndex()) {
        // Top left corner of target
        val irow = (i / squareDim) * borderlessTileDim
        val icol = (i % squareDim) * borderlessTileDim
        for (tr in 1..borderlessTileDim) {
            for (tc in 1..borderlessTileDim) {
                // if (target[irow + tr - 1][icol + tc - 1] != '?') throw Error("Render overlap")
                target[irow + tr - 1][icol + tc - 1] = tile[tr][tc]
            }
        }
    }

    return target
}

val MONSTER = 
    listOf(
        "                  # ",
        "#    ##    ##    ###",
        " #  #  #  #  #  #   ",
    ).map { it.toList() }

fun monsterSearch(t: Tile, monsters: List<Tile>): Set<Pair<Int, Int>> {
    val allMonsterSet = hashSetOf<Pair<Int, Int>>()
        
    for (tr in t.indices) {
        for (tc in t[0].indices) {

            for (monster in monsters) {
                if (tr + monster.size > t.size) continue
                if (tc + monster[0].size > t[0].size) continue
                
                var isMonster = true
                val oneMonsterSet = hashSetOf<Pair<Int, Int>>()

                monster@
                for (mr in monster.indices) {
                    for (mc in monster[0].indices) {
                        if (monster[mr][mc] == '#') {
                            if (t[tr + mr][tc + mc] == '#') {
                                oneMonsterSet.add(Pair(tr + mr, tc + mc))
                                continue
                            } else {
                                isMonster = false
                                break@monster
                            }

                        } 
                    }
                }
                if (isMonster) {
                    allMonsterSet.addAll(oneMonsterSet)
                }
            }

        }
    }

    return allMonsterSet
}

// Answer - 2256
fun processPart2() {
    val rtile = renderBorderlessGrid(solutionTiles)
    // drawTile(rtile)

    val mtrans = allTransformations(MONSTER)
    val mset = monsterSearch(rtile, mtrans)

    val allHash = rtile.map { it.count { it == '#' } }.sum()
    println("Part 2 answer - ${allHash - mset.size} = all hash ${allHash} - monster hash ${mset.size}")
}

fun parseGroup(g: List<String>): Pair<Int, List<List<Char>>> {
    val (id) = destructureWithRegex(g[0], """Tile (\d+)\:""") ?: throw Error("Bad header ${g[0]}")
    return Pair(id.toInt(), g.drop(1).map { it.toList() })
}

val groups = parseLineGroups()

val tileMap = hashMapOf<Int, List<List<Char>>>()

val parsedGroups = groups.map(::parseGroup)
parsedGroups.forEach { (id, data) -> tileMap[id] = data }

val squareDim = sqrt(parsedGroups.size.toDouble()).toInt()
val tileDim = tileMap.values.toList()[0].size

processPart1()
processPart2()
