fun main(args: Array<String>) {
    main(args[0])
}

fun main(code: String): String = compile(parse(Lexer(code)))

/**
 * turns "a = true" into `["a", "=", "true"]`
 * refactor to use strToToken
 */
class Lexer(val s: String): Iterable<Token> {
    var pos = 0
    fun peek(): Token {
        val n = s.indexOfAny(charArrayOf(' ', '\n'), pos + 1).takeIf { it != -1 } ?: s.length
        return s.substring(pos, n).toToken()
    }

    fun next(): Token {
        val n = s.indexOfAny(charArrayOf(' '), pos + 1).takeIf { it != -1 } ?: s.length
        val result = s.substring(pos, n)
        pos = n + 1
        return result.toToken()
    }

    override fun iterator(): Iterator<Token> = object : Iterator<Token> {
        override fun hasNext() = pos < s.length
        override fun next() = this@Lexer.next()
    }
    fun String.toToken() = when (this) {
        "true" -> TrueLiteral
        "=" -> Assign
        "+" -> PlusToken
        in Regex("\\d+") -> IntLiteral(toInt())
        // in Regex("_+") -> UnderscoreEntity
        in Regex("(?i)[a-z_]\\w*") -> Container(this)
        else -> TODO("huh?")
    }
     operator fun Regex.contains(s: String) = this matches s
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
