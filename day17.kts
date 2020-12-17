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

// A defaulting hash map

// You can achieve something similar in Kotlin by delegation as outlined in the commend below
// but HashMap is an open class so we can derive and override directly

/*
class Map4X<K, T>(val default: T, val m: HashMap<K, T> = hashMapOf<K, T>()) : MutableMap<K, T> by m {
    constructor(tocopy: Map4X<K, T>): this(tocopy.default, HashMap<K, T>(tocopy.m)) 

    operator override fun get(key: K) = m.get(key)
}
*/

class DefaultingHashMap<K, T> : HashMap<K, T> {
    val default: T

    constructor(default: T) { this.default = default }
    constructor(default: T, tocopy: DefaultingHashMap<K, T>) : super(tocopy) { this.default = default }
    constructor(tocopy: DefaultingHashMap<K, T>): this(tocopy.default, tocopy)

    operator override fun get(key: K) = super.get(key) ?: default
}

// How would we define a completely generic vector class?

// Common axis names for convenience

val (_x, _y, _z, _w) = listOf(0, 1, 2, 3)


// Turns out Kotlin can't do generics over different number types so
// we have to choose a concrete type

data class Vec(val values: List<Long>) : List<Long> by values {
    constructor(vararg value: Long): this(value.toList())
    constructor(vararg value: Int): this(value.toList().map { it.toLong() })

    fun isCompatible(v: Vec) = this.size == v.size

    fun assertCompatible(v: Vec) {
        if (!isCompatible(v)) throw Error("Incompatible Vecs ${this} ${v}")
    }

    override fun toString() = "V" + values.toString()

    // It's a bit naughty exposing List<Long> as an interface parent
    // then defining static overrides for + and - but the other List
    // ops will be kind of useful

    operator fun plus(v: Vec): Vec {
        assertCompatible(v)
        // return Vec(this.zip(v, { a, b -> a + b }))
        return Vec(MutableList(this.size) { i -> this[i] + v[i] })
    }

    operator fun minus(v: Vec): Vec {
        assertCompatible(v)
        return Vec(this.zip(v, { a, b -> a - b }))
    }

}

// Disambiguation overrides 

operator fun List<Vec>.plus(v: Vec): List<Vec> {
    val extended = this.toMutableList()
    extended.add(v)
    return extended
}

operator fun List<Vec>.minus(v: Vec): List<Vec> {
    val removed = this.toMutableList()
    removed.remove(v)
    return removed
}

fun allNeighbours(v: Vec): List<Vec> {
    return allNeighbours(v.size).map { dv -> v + dv }
}

fun nonDiagonalNeighbours(v: Vec): List<Vec> {
    return nonDiagonalNeighbours(v.size).map { dv -> v + dv }
}

val allNeighboursCache = hashMapOf<Int, List<Vec>>()

fun allNeighbours(dimensions: Int): List<Vec> {
    val cached = allNeighboursCache[dimensions]
    if (cached !== null) {
        return cached
    }

    val nbs = mutableListOf<Vec>()
    fun walk(i: Int, sofar: List<Long>) {
        if (i >= dimensions) {
            if (sofar.count { it == 0L } != dimensions) {
                nbs.add(Vec(sofar))
            }
            return
        }

        for (axisDelta in -1L..1L) {
            walk(i + 1, sofar + axisDelta)
        }
        
    }
    walk(0, listOf<Long>())

    allNeighboursCache[dimensions] = nbs
    return nbs
}

val allNonDiagonalNeighboursCache = hashMapOf<Int, List<Vec>>()

fun nonDiagonalNeighbours(dimensions: Int): List<Vec> {
    val cached = allNonDiagonalNeighboursCache[dimensions]
    if (cached !== null) return cached

    val result = allNeighbours(dimensions).filter { it.count { it != 0L } == 1 }
    allNonDiagonalNeighboursCache[dimensions] = result
    
    return result
}

/// Solution

enum class Cube(val display: Char) { ACTIVE('#'), INACTIVE('.') }

fun parseCube(c: Char): Cube = Cube.values().find { it.display == c } ?: throw Error("Bad input char ${c}")

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

typealias Map4 = DefaultingHashMap<Vec, Cube>

// Short of full N-dimensional generality, we could have used generics / functions to
// share the lifecycle rule logic and basic algorithm

fun runCycle4(m: DefaultingHashMap<Vec, Cube>): DefaultingHashMap<Vec, Cube> {
    val nextm = DefaultingHashMap<Vec, Cube>(m)

    for ((ov, _) in m) {
        // There's redundant calculation because of lazy windowing here but
        // it doesn't matter
        val ovnbVecs = allNeighbours(ov) + ov

        for (v in ovnbVecs) {
            val cube = m[v] 
            val nbActive = allNeighbours(v).count { m[it] === Cube.ACTIVE }
            when (cube) {
                Cube.ACTIVE -> {
                    if (nbActive !in 2..3) nextm[v] = Cube.INACTIVE
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
    val map = Map4(Cube.INACTIVE)

    for ((i, row) in data.withIndex()) {
        for ((j, cell) in row.withIndex()) {
            val v = Vec(j, -i, 0, 0)
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

val data = parseLinesAsGridAndMap(::parseCube)
processPart1()
processPart2()
