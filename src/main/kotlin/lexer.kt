//TODO: put tokens here (theyre currently coupled with expressions)

/**
 * turns "a = true" into `["a", "=", "true"]`
 * refactor to use strToToken
 */
class Lexer(val s: String) : Iterable<Token> {
    var pos = 0
    fun peek(): Token {
        val n = s.indexOfAny(charArrayOf(' ', '\n'), pos + 1).takeIf { it != -1 } ?: s.length
        return s.substring(pos, n).toToken()
    }

    fun next(): Token {
        val n = try {
            s.indexOfAny(charArrayOf(' '), pos + 1).takeIf { it != -1 } ?: s.length
        } catch (t: Throwable) {
            throw Exception("tried to read past end of file")
        }
        val result = s.substring(pos, n)
        pos = n + 1
        return result.toToken()
    }

    fun hasNext() = pos < s.length

    override fun iterator(): Iterator<Token> = object : Iterator<Token> {
        override fun hasNext() = pos < s.length
        override fun next() = this@Lexer.next()
    }

    fun String.toToken() = when (this) {
        "var" -> VarToken
        "true" -> TrueLiteral
        "=" -> Assign
        "==" -> EqualsToken
        "+" -> PlusToken
        "{" -> LeftBrace
        "}" -> RightBrace
        in Regex("\\d+") -> IntLiteral(toInt())
        // in Regex("_+") -> UnderscoreEntity
        in Regex("(?i)[a-z_]\\w*") -> Container(this /*place holder?*/)
        else -> TODO("huh?")
    }

    operator fun Regex.contains(s: String) = this matches s
}
