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
        val n = s.indexOfAny(charArrayOf(' '), pos + 1).takeIf { it != -1 } ?: s.length
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
        in Regex("\\d+") -> IntLiteral(toInt())
        // in Regex("_+") -> UnderscoreEntity
        in Regex("(?i)[a-z_]\\w*") -> Container(this /*place holder?*/)
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
fun compile(entity: Entity): String {
    TODO("LOOL!")
}

/**
 * Checks the parsed code is valid.
 *
 * How does this link back to where the error is in source?
 *
 * CompilerVariable(name="a", staticType=BooleanType, valueType=BooleanType, value=BooleanType(true))
 */
fun check(l: List<*>): List<Error> = TODO("impl check")

//class Scope {
//    val things = mutableMapOf<String, Any>()
//}
//
//class CallStack {
//    val scopes = mutableListOf(Scope())
//    fun addThing(entity: NamedEntity) {
//        scopes.last().things[entity.name] = entity
//    }
//    fun newScope() { scopes.add(Scope()) }
//    fun endScope() { scopes.removeLast() }
//    operator fun set(name: String, value: Any) {
//        scopes.last().things[name] = value
//    }
//    operator fun get(name: String) = scopes.last().things[name]
//}


//fun interpret(l: Lexer) {
//    val stack = CallStack()
//    while (l.hasNext()) {
//        when (val it = doParse(l)) {
//            is VarEntity -> stack.addThing(it.container)
//            is Assignment -> stack[it.lhs.name] = eval(it.rhs)
//        }
//    }
//}
//
//fun eval(e: Expression<*>): Any {
//    when (e) {
//        is
//    }
//}
//
//fun doParse(l: Lexer): Entity = when (val t = l.next()) {
//    Var -> parseVar(l)
//    is Container -> parseContainer(l, t)
//    is TrueLiteral -> t
//    in Builtins ->
//    else -> TODO("parsed some cringe $t")
//}
//
//val Builtins = mapOf(
//    ("print") to { it: Any -> println(it) },
//)
//































