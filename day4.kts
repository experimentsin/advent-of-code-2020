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

fun isValidPassport(pp: Passport): Boolean {
    println("isValidPassport: $pp");
    for (field in hackedRequiredFields) {
        if (pp.fields[field] == null) {
            println("Missing $field");
            return false;
        }
    }
    return true;
}

fun processPart1() {
    println("Passports $passports");
    val valid = passports.count({ isValidPassport(it) });
    println("Valid $valid");
}

val passportTexts = loadPassports();
val passports = compilePassports(passportTexts);

processPart1();