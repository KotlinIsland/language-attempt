fun main(args: Array<String>) {
    main(args[0])
}

fun main(code: String): String = compile(parse(Lexer(code)))

/**
 * turns "a = true" into `["a", "=", "true"]`
 * maybe it can return tokens not strings
 * maybe it can be an iterable?
 */
class Lexer(val s: String) {
    var pos = 0
    fun next(): String? {
        if (pos >= s.length) return null
        val n = s.indexOfAny(charArrayOf(' '), pos + 1).takeIf { it != -1 } ?: s.length
        val result = s.substring(pos, n)
        pos = n + 1
        return result
    }
}

/**
 * turns `Lexer returning ... ["a", "=", "true"]` into `[Assignment(Container(name="a"), BooleanTrue)]`
 */
fun parse(l: Lexer): List<*> = TODO("Figure out where this stands in the parse tree")

@ExperimentalStdlibApi
fun parseModule(l: Lexer): Module {
    return Module(buildList {
        while (true) add(
            when (val next = l.next()) {
                null -> break
                // parse for all other bits like statements
                else -> parseContainer(l, Container(next))
            }
        )
    })
}

fun parseContainer(l: Lexer, c: Container): Assignment {
    return when (val n = l.next()) {
        "=" -> Assignment(c, parseExpression(l))
        else -> TODO("parseContainer for $n")
    }
}

fun parseExpression(l: Lexer): BooleanType {
    return when (val n = l.next()) {
        "true" -> BooleanType(true)
        else -> TODO("parseExpression for $n")
    }
}

/**
 * turns `[Assignment(Container(name="a"), BooleanTrue)]` into """var a = true"""
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
