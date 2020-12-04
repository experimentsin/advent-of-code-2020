fun loadPassports(): List<String> {
    val passports = mutableListOf<String>();
    var passportLines = "";

    fun commitPassport() {
        if (!passportLines.isEmpty()) {
            passports.add(passportLines);
            passportLines = "";            
        }
    }

    while (true) {
        val line = readLine();

        // End of file
        if (line == null) {
            commitPassport();
            return passports;
        }

        val trimmed = line.trim();

        // Blank line
        if (trimmed.isEmpty()) {
            commitPassport();
            continue;
        }

        // Line with content
        if (!passportLines.isEmpty()) passportLines += " ";
        passportLines += line.trim();
    }
}

data class Passport (val fields: HashMap<String, String> = hashMapOf<String, String>());

fun compilePassports(texts: List<String>): List<Passport> {
    fun compilePassport(text: String): Passport {
        val pp = Passport();
        val fieldTexts = text.split(" ");
        for (fieldText in fieldTexts) {
            val (key, value) = fieldText.split(":");
            pp.fields[key] = value;
        }
        return pp;
    }

    return texts.map(::compilePassport);
}

val normalRequiredFields = listOf(
    "byr",
    "iyr",
    "eyr",
    "hgt",
    "hcl",
    "ecl",
    "pid",
    "cid"
);

val hackedRequiredFields = normalRequiredFields.toMutableList();
hackedRequiredFields.removeLast();

fun isValidPassportPart1(pp: Passport): Boolean {
    println("isValidPassport: $pp");
    for (field in hackedRequiredFields) {
        if (pp.fields[field] == null) {
            println("Missing $field");
            return false;
        }
    }
    return true;
}

data class FieldSpec(val name: String, val validate: (String) -> Boolean);

fun inInclusiveRange(trial: Int, low: Int, high: Int): Boolean = low <= trial && trial <= high;

val byrSpec = FieldSpec("byr", fun (value: String): Boolean {
    val pattern = """(\d\d\d\d)""".toRegex();
    val match = pattern.matchEntire(value);
    if (match === null) return false;

    val (year) = match.destructured;
    return inInclusiveRange(year.toInt(), 1920, 2002);
} );

val iyrSpec = FieldSpec("iyr", fun (value: String): Boolean {
    val pattern = """(\d\d\d\d)""".toRegex();
    val match = pattern.matchEntire(value);
    if (match === null) return false;

    val (year) = match.destructured;
    return inInclusiveRange(year.toInt(), 2010, 2020);
} );

val eyrSpec = FieldSpec("eyr", fun (value: String): Boolean {
    val pattern = """(\d\d\d\d)""".toRegex();
    val match = pattern.matchEntire(value);
    if (match === null) return false;

    val (year) = match.destructured;
    return inInclusiveRange(year.toInt(), 2020, 2030);
} );

val hgtSpec = FieldSpec("hgt", fun (value: String): Boolean {
    val pattern = """(\d+)(.+)""".toRegex();
    val match = pattern.matchEntire(value);
    if (match === null) return false;

    val (height, unit) = match.destructured;
    val iheight = height.toInt();
    when (unit) {
        "cm" -> return inInclusiveRange(iheight, 150, 193)
        "in" -> return inInclusiveRange(iheight, 59, 76)
        else -> return false;
    }
} );

val hclSpec = FieldSpec("hcl", fun (value: String): Boolean {
    val pattern = """#([a-f0-9]*)""".toRegex();
    val match = pattern.matchEntire(value);
    if (match === null) return false;

    val (rgb) = match.destructured;
    return rgb.length == 6;    
} );

val eclSpec = FieldSpec("ecl", fun (value: String): Boolean {
    val pattern = """(.*)""".toRegex();
    val match = pattern.matchEntire(value);
    if (match === null) return false;

    val (col) = match.destructured;
    when (col) {
        "amb", "blu", "brn", "gry", "grn", "hzl", "oth" -> return true
        else -> return false;
    }
} );

val pidSpec = FieldSpec("pid", fun (value: String): Boolean {
    val pattern = """(\d\d\d\d\d\d\d\d\d)""".toRegex();
    val match = pattern.matchEntire(value);
    if (match === null) return false;

    return true;
} );

val cidSpec = FieldSpec("cid", fun (_: String): Boolean {
    return true;
} );

val requiredFieldsPart2 = listOf(byrSpec, iyrSpec, eyrSpec, hgtSpec, hclSpec, eclSpec, pidSpec);

fun isValidPassportPart2(pp: Passport): Boolean {
    println("isValidPassportPart2: $pp");
    for (field in requiredFieldsPart2) {
        if (pp.fields[field.name] == null) {
            println("Missing $field");
            return false;
        }
        if (!field.validate(pp.fields[field.name]!!)) {
            println("Validation failed $field");
            return false;          
        }
    }
    return true;
}

fun testSpecs() {

    fun checkValid(spec: FieldSpec, input: String) {
        if (!spec.validate(input)) throw Error("checkValid failed $spec $input");
    }
    fun checkInvalid(spec: FieldSpec, input: String) {
        if (spec.validate(input)) throw Error("checkInvalid failed $spec $input");
    }
    fun checkAllValid(spec: FieldSpec, vararg input: String) {
        input.forEach({ checkValid(spec, it)})
    }
    fun checkAllInvalid(spec: FieldSpec, vararg input: String) {
        input.forEach({ checkInvalid(spec, it)})
    }

    checkAllValid(byrSpec, "1920", "2002");
    checkAllInvalid(byrSpec, "1919", "2003", "ABCD", "200", "20000");

    checkAllValid(iyrSpec, "2010", "2020");
    checkAllInvalid(iyrSpec, "2009", "2021", "ABCD", "200", "20000");

    checkAllValid(eyrSpec, "2020", "2030");
    checkAllInvalid(eyrSpec, "2019", "2031", "ABCD", "200", "20000");

    checkAllValid(hgtSpec, "150cm", "193cm", "59in", "76in");
    checkAllInvalid(hgtSpec, "149cm", "194cm", "58in", "77in", "100c", "100cmx", "100i", "100inx", "100ab");

    checkAllValid(hclSpec, "#1a2b3c", "#a1b1c1", "#123456", "#789012", "#abcdef");
    checkAllInvalid(hclSpec, "#1a", "#1a2b", "#1a2b3c4e", "#1x2y3z", "1a2b3c");

    checkAllValid(eclSpec, "amb", "blu", "brn", "gry", "grn", "hzl", "oth");
    checkAllInvalid(eclSpec, "", "am", "ambblue", "ambo", " amb ");

    checkAllValid(pidSpec, "123456789", "012345678");
    checkAllInvalid(pidSpec, "12345678", "1234567890", "abcdefghi");

}

// Answer: 226
fun processPart1() {
    println("Passports $passports");
    val valid = passports.count({ isValidPassportPart1(it) });
    println("Valid $valid");
}

fun processPart2() {
    println("Passports $passports");
    val valid = passports.count({ isValidPassportPart2(it) });
    println("Valid $valid");
}

val passportTexts = loadPassports();
val passports = compilePassports(passportTexts);

processPart1();
testSpecs();
processPart2();