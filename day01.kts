val input = mutableListOf<Int>()

fun processInput() {
    while (true) {
        val line = readLine()
        if (line == null) break
        input.add(line.toInt())
    }
}

fun processPart1() {
    val len = input.count()
    for (i in 0 until len) {
        for (j in i until len) {
            val inputi = input[i]
            val inputj = input[j]
            if (inputi + inputj == 2020) {
                println("Sum " + inputi + " " + inputj)
                println("Product " + (inputi * inputj))
            }
        }
    }
}

fun processPart2() {
    val len = input.count()
    for (i in 0 until len) {
        for (j in i + 1 until len) {
            for (k in j + 1 until len) {
    	        val inputi = input[i]
        	val inputj = input[j]
                val inputk = input[k]

          	if (inputi + inputj + inputk == 2020) {
           	    println("Sum " + inputi + " " + inputj + " " + inputk)
          	    println("Product " + (inputi * inputj * inputk))
          	}
            }
        }
    }
}

processInput()
processPart1()
processPart2()
