import org.intellij.lang.annotations.Language

// Tokens
// The universe of tokens consists of singletons { keywords, operators, whitespace } and
//  group values like Ints { 1, 2, 3 ... } and symbols { foo, bar ... }
interface Token

interface InfixToken : Token

// TODO: give tokens their pattern here
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
object EOF : Token

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
        pos = it.index
        current = it
    }.token

    private fun nextPosition() = try {
        // get the position directly after the current token
        val indexBeforeWhitespace = if (this::current.isInitialized) pos + current.length else 0
        // skip over any spaces/newlines
        val indexAfterWhitespace = indexBeforeWhitespace + Regex("^[\n ]*").find(s.substring(indexBeforeWhitespace))!!.value.length
        val stringFromStartOfNextToken = s.substring(indexAfterWhitespace)
        if (stringFromStartOfNextToken.isEmpty()) LexerPosition(indexAfterWhitespace, EOF, 0)
        else {
            val (token, length) = stringFromStartOfNextToken.toToken()
            LexerPosition(indexAfterWhitespace, token, length)
        }
    } catch (t: Throwable) { // TODO get correct type of exception here
        throw Exception("Tried to read past end of file $t")
    }

    fun hasNext() = pos < s.length

    fun parseIfHasNext(entity: Entity, parser: (Entity) -> Entity) = if (hasNext()) parser(entity) else entity

    override fun iterator(): Iterator<Token> = object : Iterator<Token> {
        override fun hasNext() = pos < s.length
        override fun next() = this@Lexer.next()
    }
    @Suppress("NOTHING_TO_INLINE")
    inline fun String.startsWith1(pre: Regex) = pre.containsMatchIn(this)
    @Suppress("NOTHING_TO_INLINE")
    inline fun String.startsWith1(@Language("REGEXP") pre: String) = startsWith1(Regex1(pre))
    @Suppress("NOTHING_TO_INLINE")
    inline fun Regex1(@Language("REGEXP") blah: String) = Regex("^$blah")
    fun String.toToken(): Pair<Token, Int> = when {
        startsWith1("var\\b") -> VarToken to 3
        startsWith1("true\\b") -> TrueToken to 4
        startsWith1("false\\b") -> FalseToken to 5
        startsWith1("if\\b") -> IfToken to 2
        startsWith1("==") -> EqualsToken to 2
        startsWith1("=") -> Assign to 1
        startsWith1("!=") -> NotEqualsToken to 2
        startsWith1("\\+") -> PlusToken to 1
        startsWith1("\\(") -> LeftParenthesis to 1
        startsWith1("\\)") -> RightParenthesis to 1
        startsWith1("\\[") -> LeftBracket to 1
        startsWith1("]") -> RightBracket to 1
        startsWith1("\\{") -> LeftBrace to 1
        startsWith1("}") -> RightBrace to 1
        startsWith1(Regex1("\n+")) -> NewlineToken to 1 // HACK
        startsWith1("\\d+") -> Regex("\\w+").find(this)!!.value.let { IntLiteralToken(it.toInt()) to it.length }
        startsWith1("(?i)[a-z_]\\w*") -> Regex("\\w+").find(this)!!.value.let { ContainerToken(it) to it.length }
        else -> error("lexed something wacky '$this'")
    }

    operator fun Regex.contains(s: String) = this matches s
}
