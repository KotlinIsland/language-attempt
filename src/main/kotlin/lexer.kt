// TODO: put tokens here (theyre currently coupled with expressions)

private data class LexerPosition(val index: Int, val token: Token)

/**
 * turns "a = true" into `["a", "=", "true"]`
 * refactor to use strToToken
 */
class Lexer(val s: String) : Iterable<Token> {
    var pos = 0
    fun peek(): Token = nextPosition().token

    fun next(): Token = nextPosition().also { pos = it.index + 1 }.token

    private fun nextPosition() = try {
        val index = s.indexOfAny(charArrayOf(' '), pos + 1).takeIf { it != -1 } ?: s.length
        LexerPosition(index, s.substring(pos, index).toToken())
    } catch (t: Throwable) {
        throw Exception("tried to read past end of file")
    }

    fun hasNext() = pos < s.length

    fun parseIfHasNext(entity: Entity, parser: (Entity) -> Entity) = if (hasNext()) parser(entity) else entity

    override fun iterator(): Iterator<Token> = object : Iterator<Token> {
        override fun hasNext() = pos < s.length
        override fun next() = this@Lexer.next()
    }

    fun String.toToken() = when (this) {
        "var" -> VarToken
        "true" -> TrueToken
        "false" -> FalseToken
        "if" -> IfToken
        "=" -> Assign
        "==" -> EqualsToken
        "!=" -> NotEqualsToken
        "+" -> PlusToken
        "(" -> LeftParenthesis
        ")" -> RightParenthesis
        "[" -> LeftBracket
        "]" -> RightBracket
        "{" -> LeftBrace
        "}" -> RightBrace
        in Regex("\n+") -> NewlineToken
        in Regex("\\d+") -> IntLiteral(toInt())
        // in Regex("_+") -> UnderscoreEntity
        in Regex("(?i)[a-z_]\\w*") -> Container(this /*place holder?*/)
        else -> TODO("huh?")
    }

    operator fun Regex.contains(s: String) = this matches s
}
