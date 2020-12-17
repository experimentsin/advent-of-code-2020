/// Boilerplate

import kotlin.math.*

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

enum class Cube(val display: Char) { ACTIVE('#'), INACTIVE('.') }

fun parseCell(c: Char): Cube {
    return when (c) {
        '#' -> Cube.ACTIVE
        '.' -> Cube.INACTIVE
        else -> throw Error("Error bad input char ${c}")
    }
}

// x > and y ^ and z away

data class Vec3(val x: Int, val y: Int, val z: Int)

fun neighbourVecs3(): List<Vec3> {
    val vecs = mutableListOf<Vec3>()
    for (x in -1..1) {
        for (y in -1..1) {
            for (z in -1..1) {
                if (x == 0 &&  y == 0 && z == 0) continue

                vecs.add(Vec3(x, y, z))
            }
        }
    }

    if (vecs.size != 3*3*3 - 1) throw Error("Bad neighbourVecs")

    return vecs
}

val NEIGHBOUR_VECS3 = neighbourVecs3()

typealias Map3 = HashMap<Vec3, Cube>

fun drawMap3(m: Map3) {
    println("--")
    println("Vals: ${m.entries}")
    println("--")
    var minx = Int.MAX_VALUE
    var maxx = Int.MIN_VALUE

    var miny = Int.MAX_VALUE
    var maxy = Int.MIN_VALUE

    var minz = Int.MAX_VALUE
    var maxz = Int.MIN_VALUE

    for ((k, _) in m) {
        minx = min(minx, k.x)
        maxx = max(maxx, k.x)

        miny = min(miny, k.y)
        maxy = max(maxy, k.y)

        minz = min(minz, k.z)
        maxz = max(maxz, k.z)
    }

    for (z in minz..maxz) {
        println("z=${z}")
        for (y in maxy downTo miny) {
            for (x in minx..maxx) {
                val cell = m[Vec3(x, y, z)]
                if (cell !== null) {
                    print("${cell.display}")
                } else {
                    print(".")
                }
            }
            println("")
        }
    }
    
}

fun runCycle3(m: Map3): Map3 {
    val nextm = Map3(m)

    for ((ov, _) in m) {
        // There's redundant calculation here but it doesn't matter
        val ovnbVecs = NEIGHBOUR_VECS3.map { dv -> Vec3(ov.x + dv.x, ov.y + dv.y, ov.z + dv.z) } + ov

        for (v in ovnbVecs) {
            val cube = m[v] ?: Cube.INACTIVE
            val nbCubes = NEIGHBOUR_VECS3.map { dv -> m[Vec3(v.x + dv.x, v.y + dv.y, v.z + dv.z)] ?: Cube.INACTIVE }
            val nbActive = nbCubes.filter { it == Cube.ACTIVE }.count()
            when (cube) {
                Cube.ACTIVE -> {
                    if (nbActive < 2 || nbActive > 3) nextm[v] = Cube.INACTIVE
                }
                Cube.INACTIVE -> {
                    if (nbActive == 3) nextm[v] = Cube.ACTIVE
                }
            }
        }
    }

    return nextm
}

// Answer - 284
fun processPart1() {
    println("NVs ${NEIGHBOUR_VECS3}")

    val map = Map3()

    for ((i, row) in data.withIndex()) {
        for ((j, cell) in row.withIndex()) {
            val v = Vec3(j, -i, 0)
            map[v] = cell
        }
    }

    // drawMap3(map)

    var nextm = map
    for (i in 1..6) {
        nextm = runCycle3(nextm)
        // drawMap3(nextm)
    }

    val count = nextm.values.filter { it == Cube.ACTIVE }.count()

    println("Part 1 answer - ${count} cubes active after 6 iterations")
}

// There is an obvious generalisation to N dimensions here that also allows logic sharing
// e.g. having a hash map representation of vectors, but I just did the lazy thing: copy,
// paste, and tweak

data class Vec4(val x: Int, val y: Int, val z: Int, val w: Int)

fun neighbourVecs4(): List<Vec4> {
    val vecs = mutableListOf<Vec4>()
    for (x in -1..1) {
        for (y in -1..1) {
            for (z in -1..1) {
                for (w in -1..1) {
                    if (x == 0 &&  y == 0 && z == 0 && w == 0) continue
                    vecs.add(Vec4(x, y, z, w))
                }
            }
        }
    }

    if (vecs.size != 3*3*3*3 - 1) throw Error("Bad neighbourVecs 4D")

    return vecs
}

val NEIGHBOUR_VECS4 = neighbourVecs4()

typealias Map4 = HashMap<Vec4, Cube>

// Short of full N-dimensional generality, we could have used generics / functions to
// share the lifecycle rule logic and basic algorithm

fun runCycle4(m: Map4): Map4 {
    val nextm = Map4(m)

    for ((ov, _) in m) {
        // There's redundant calculation here but it doesn't matter
        val ovnbVecs = NEIGHBOUR_VECS4.map { dv -> Vec4(ov.x + dv.x, ov.y + dv.y, ov.z + dv.z, ov.w + dv.w) } + ov

        for (v in ovnbVecs) {
            val cube = m[v] ?: Cube.INACTIVE
            val nbCubes = NEIGHBOUR_VECS4.map { dv -> m[Vec4(v.x + dv.x, v.y + dv.y, v.z + dv.z, v.w + dv.w)] ?: Cube.INACTIVE }
            val nbActive = nbCubes.filter { it == Cube.ACTIVE }.count()
            when (cube) {
                Cube.ACTIVE -> {
                    if (nbActive < 2 || nbActive > 3) nextm[v] = Cube.INACTIVE
                }
                Cube.INACTIVE -> {
                    if (nbActive == 3) nextm[v] = Cube.ACTIVE
                }
            }
        }
    }

    return nextm
}

// Answer - 2240
fun processPart2() {
    println("NVs ${NEIGHBOUR_VECS4}")

    val map = Map4()

    for ((i, row) in data.withIndex()) {
        for ((j, cell) in row.withIndex()) {
            val v = Vec4(j, -i, 0, 0)
            map[v] = cell
        }
    }

    var nextm = map
    for (i in 1..6) {
        nextm = runCycle4(nextm)
    }

    val count = nextm.values.filter { it == Cube.ACTIVE }.count()

    println("Part 2 answer - ${count} cubes active after 6 iterations")
}

val data = parseLinesAsGridAndMap(::parseCell)
processPart1()
processPart2()
