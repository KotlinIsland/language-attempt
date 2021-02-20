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

data class LexerPosition(val index: Int, val token: Token, val length: Int)

/**
 * turns "a = true" into `["a", "=", "true"]`
 * refactor to use strToToken
 */
class Lexer(val s: String) : Iterable<Token> {
    var pos = 0
    lateinit var current: LexerPosition

    fun peek(): Token = nextPosition().token

    fun next(): Token = nextPosition().also {
        pos = it.index + 1
        current = it
    }.token

    /**
     * solution is to use [toTOken] logic to determine if
     * if matcher is a string then use it literally
     * if matcher is a Regex then regex match it obv.
     */
    /**
     * the start of the next token "a{90)asf.foo 1"
     */
    private fun nextPosition() = try {
        // get the position directly after the current token
        val indexBeforeWhitespace = pos + current.length
        // skip over any spaces/newlines
        val indexAfterWhitespace = indexBeforeWhitespace + Regex("^[\n ]*").find(s, indexBeforeWhitespace)!!.value.length
        val `things that don't break tokens` = setOf("a-z", "0-9", "_")
        val `long tokens that look funny` = setOf("!=", "==", "!==", "===")
        val tokenString = s.substring(indexAfterWhitespace, Regex("^[\n ]*").find(s))
        LexerPosition(indexAfterWhitespace, tokenString.toToken(), tokenString.length)
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
}
