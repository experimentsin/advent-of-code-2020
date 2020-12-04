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

val requiredFieldsPart1 = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid");

fun isValidPassportPart1(pp: Passport): Boolean {
    // println("isValidPassport: $pp");
    for (field in requiredFieldsPart1) {
        if (pp.fields[field] == null) {
            // println("Missing $field");
            return false;
        }
    }
    return true;
}

/*
    This pattern could be nicer but many formulations triggers internal errors in 
    the Kotlin compiler similar to https://youtrack.jetbrains.com/issue/KT-24457 

 */

abstract class FieldSpec(val name: String, val pattern: String) {

    val regex = pattern.toRegex();

    fun validate(input: String): Boolean {
        val match = regex.matchEntire(input);
        if (match === null) return false;

        val result = validateMatch(match.destructured);
        return result;
    }

    fun inRange(trial: Int, low: Int, high: Int): Boolean = low <= trial && trial <= high;

    abstract fun validateMatch(match: MatchResult.Destructured): Boolean;
}

object ByrSpec : FieldSpec("byr", """(\d\d\d\d)""") {
    override fun validateMatch(match: MatchResult.Destructured): Boolean {
        val (year) = match;
        return inRange(year.toInt(), 1920, 2002);
     }
}

object IyrSpec : FieldSpec("iyr", """(\d\d\d\d)""") {
    override fun validateMatch(match: MatchResult.Destructured): Boolean {
        val (year) = match;
        return inRange(year.toInt(), 2010, 2020);
     }
}

object EyrSpec : FieldSpec("eyr", """(\d\d\d\d)""") {
    override fun validateMatch(match: MatchResult.Destructured): Boolean {
        val (year) = match;
        return inRange(year.toInt(), 2020, 2030);
     }
}

object HgtSpec : FieldSpec("hgt", """(\d+)(.+)""") {
    override fun validateMatch(match: MatchResult.Destructured): Boolean {
        val (height, unit) = match;
        val iheight = height.toInt();
        when (unit) {
            "cm" -> return inRange(iheight, 150, 193)
            "in" -> return inRange(iheight, 59, 76)
            else -> return false;
        }
    }
}

object HclSpec : FieldSpec("hcl", """#([a-f0-9]*)""") {
    override fun validateMatch(match: MatchResult.Destructured): Boolean {
        val (rgb) = match;
        return rgb.length == 6;    
    }
}

object EclSpec : FieldSpec("ecl", """(.*)""") {
    override fun validateMatch(match: MatchResult.Destructured): Boolean {
        val (col) = match;
        when (col) {
            "amb", "blu", "brn", "gry", "grn", "hzl", "oth" -> return true
            else -> return false
        }
    }
}

object PidSpec : FieldSpec("pid", """(\d\d\d\d\d\d\d\d\d)""") {
    override fun validateMatch(match: MatchResult.Destructured): Boolean {
        return true;
    }
}

val requiredFieldsPart2 = listOf(ByrSpec, IyrSpec, EyrSpec, HgtSpec, HclSpec, EclSpec, PidSpec)

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

    checkAllValid(ByrSpec, "1920", "2002");
    checkAllInvalid(ByrSpec, "1919", "2003", "ABCD", "200", "20000");

    checkAllValid(IyrSpec, "2010", "2020");
    checkAllInvalid(IyrSpec, "2009", "2021", "ABCD", "200", "20000");

    checkAllValid(EyrSpec, "2020", "2030");
    checkAllInvalid(EyrSpec, "2019", "2031", "ABCD", "200", "20000");

    checkAllValid(HgtSpec, "150cm", "193cm", "59in", "76in");
    checkAllInvalid(HgtSpec, "149cm", "194cm", "58in", "77in", "100c", "100cmx", "100i", "100inx", "100ab");

    checkAllValid(HclSpec, "#1a2b3c", "#a1b1c1", "#123456", "#789012", "#abcdef");
    checkAllInvalid(HclSpec, "#1a", "#1a2b", "#1a2b3c4e", "#1x2y3z", "1a2b3c");

    checkAllValid(EclSpec, "amb", "blu", "brn", "gry", "grn", "hzl", "oth");
    checkAllInvalid(EclSpec, "", "am", "ambblue", "ambo", " amb ");

    checkAllValid(PidSpec, "123456789", "012345678");
    checkAllInvalid(PidSpec, "12345678", "1234567890", "abcdefghi");

}

fun isValidPassportPart2(pp: Passport): Boolean {
    for (field in requiredFieldsPart2) {
        if (pp.fields[field.name] == null) {
            // println("Missing $field");
            return false;
        }
        if (!field.validate(pp.fields[field.name]!!)) {
            // println("Validation failed $field");
            return false;          
        }
        // println("Validation succeeded $field");
    }
    return true;
}

// Answer: 226
fun processPart1() {
    // println("Passports $passports");
    val valid = passports.count({ isValidPassportPart1(it) });
    println("Valid $valid");
}

// Answer: 160
fun processPart2() {
    // println("Passports $passports");
    val valid = passports.count({ isValidPassportPart2(it) });
    println("Valid $valid");
}

val passportTexts = loadPassports();
val passports = compilePassports(passportTexts);

processPart1();
testSpecs();
processPart2();
