fun main(args: Array<String>) {
    main(args[0])
}

fun main(code: String): String = compile(parse(lex(code)))

/**
 * turns "a = true" into ["a", "=", "true"]
 */
fun lex(s: String): List<String> = TODO("impl lex")
/**
 * turns ["a", "=", "true"] into [Assignment(Container(name="a"), BooleanTrue)]
 */
fun parse(l: List<String>): List<*> = TODO("impl parse")
/**
 * turns [Assignment(Container(name="a"), BooleanTrue)] into """var a = true"""
 */
fun compile(l: List<*>): String = TODO("impl compile")

/**
 * Checks the parsed code is valid.
 *
 * How does this link back to where the error is in source?
 *
 * CompilerVariable(name="a", staticType=BooleanType, valueType=BooleanType, value=BooleanType(true))
 */
fun check(l: List<*>): List<Error> = TODO("impl check")
