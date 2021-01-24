// Tokens
// The universe of tokens consists of singletons { keywords, operators, whitespace } and
//  group values like Ints { 1, 2, 3 ... } and symbols { foo, bar ... }
interface Token

interface InfixToken : Token

object PlusToken : Token, InfixToken
object Assign : Token
object LeftBrace : Token
object RightBrace : Token
object EqualsToken : Token, InfixToken
object NotEqualsToken : Token
object LeftParenthesis : Token
object RightParenthesis : Token
object LeftBracket : Token
object RightBracket : Token
object IfToken : Token
object VarToken : Token
object TrueToken : Token
object FalseToken : Token
object NewlineToken : Token

data class IntLiteralToken(val value: Int) : Token
data class ContainerToken(val name: String) : Token

private data class LexerPosition(val index: Int, val token: Token)

/**
 * turns "a = true" into `["a", "=", "true"]`
 * refactor to use strToToken
 */
class Lexer(val s: String) : Iterable<Token> {
    var pos = 0
    lateinit var current: Token

    fun peek(): Token = nextPosition().token

    fun next(): Token = nextPosition().also {
        pos = it.index + 1
        current = it.token
    }.token

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

    fun String.toToken(): Token = when (this) {
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
        in Regex("\\d+") -> IntLiteralToken(toInt())
        // in Regex("_+") -> UnderscoreEntity
        in Regex("(?i)[a-z_]\\w*") -> ContainerToken(this /*place holder?*/)
        else -> TODO("huh?")
    }

    operator fun Regex.contains(s: String) = this matches s

    fun gobbleNewlines(): Token {
        while (current is NewlineToken)
            next()
        return current
    }
}
