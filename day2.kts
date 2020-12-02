
fun processInput() {
    while (true) {
        val line = readLine();
        if (line == null) break;

        val (start, end, chr, pwd) = parseLine(line);

        println("Parsed: $start $end $chr $pwd");
        processLineRule2(start.toInt(), end.toInt(), chr[0], pwd);
    }

    println("Valid $valid");
    println("Invalid $invalid");
}

var valid: Int = 0;
var invalid: Int = 0;

// Answer: 550
fun processLineRule1(start: Int, end: Int, chr: Char, pwd: String) {
    val count = pwd.count({ it == chr });
    if (start <= count && count <= end) {
        println("YES");
        ++valid;
    } else {
        println("NO");
        ++invalid;
    }
}

// Answer: 634
fun processLineRule2(start1based: Int, end1based: Int, chr: Char, pwd: String) {
    val start = start1based - 1; val end = end1based - 1;

    var count = 0;

    if (start < pwd.length && pwd[start] == chr) {
        ++count;
    }

    if (end < pwd.length && pwd[end] == chr) {
        ++count;
    }

    if (count == 1) {
        println("YES");
        ++valid;
    } else {
        println("NO");
        ++invalid;
    }
}

/* 
fun parseLine(line: String): List<String> {
    var (spec, pwd) = line.split(":");
    pwd = pwd.trim();

    var (range, chr) = spec.split(" ");
    range = range.trim(); chr = chr.trim();

    var (start, end) = range.split("-");

    return listOf(start, end, chr, pwd);
}
*/

val lineSpec = """(\d+)\-(\d+) (.)\: (.*)""".toRegex();

fun parseLine(line: String): List<String> {
    val matches = lineSpec.find(line);
    if (matches == null) {
        throw Error("Failed to parse line: <$line>");
    }

    /*
    val (start, end, chr, pwd) = matches.destructured;
    return listOf(start, end, chr, pwd);
    */

    return matches.destructured.toList();
}

// Only start processing once all top level vars
// have been initialised (notably the regex)
processInput();
