/**
 * turns `Lexer returning ... ["a", "=", "true"]` into `[Assignment(Container(name="a"), BooleanTrue)]`
 */
fun parse(l: Lexer): List<Entity> {
    val result = mutableListOf<Entity>()
    while (l.hasNext()) {
        result.add(l.startParsing())
    }
    return result
}

/**
 * turns `[Assignment(Container(name="a"), BooleanTrue)]` into """var a = true"""
 */
fun compile(es: List<Entity>) = es.joinToString("\n") { it.compile() }

/**
 * Checks the parsed code is valid.
 *
 * CompilerVariable(name="a", staticType=BooleanType, valueType=BooleanType, value=BooleanType(true))
 */
fun check(l: List<Entity>): List<Error> {
    return listOf()
}
class CheckException : Exception()
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































