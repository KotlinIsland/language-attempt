fun main(args: Array<String>) {
    main(args[0])
}

"""
a = true
if a == true {
  a
}
"""

fun main(code: String): String = compile(parse(Lexer(code)))

/**
 * turns "a = true" into `["a", "=", "true"]`
 * maybe it can return tokens not strings
 * maybe it can be an iterable?
 */
class Lexer(val s: String): Iterable<String> {
    var pos = 0
    fun peek(): String {
        val n = s.indexOfAny(charArrayOf(' ', '\n'), pos + 1).takeIf { it != -1 } ?: s.length
        return s.substring(pos, n)
    }
    fun next(): String {
        val n = s.indexOfAny(charArrayOf(' '), pos + 1).takeIf { it != -1 } ?: s.length
        val result = s.substring(pos, n)
        pos = n + 1
        return result
    }

    override fun iterator(): Iterator<String> = object : Iterator<String> {
        override fun hasNext() = pos < s.length
        override fun next() = this@Lexer.next()
    }
}

/**
 * turns `Lexer returning ... ["a", "=", "true"]` into `[Assignment(Container(name="a"), BooleanTrue)]`
 */
fun parse(l: Lexer): List<*> = TODO("Figure out where this stands in the parse tree")



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
