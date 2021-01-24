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
 */
fun check(l: List<Entity>): List<CheckException> {
    val errors = mutableListOf<CheckException>()
    val scope = Scope()
    for (el in l) {
        if (el is ContainerDeclaration) {
            // check redeclarations
            if (el.container.name in scope.symbols.keys)
                errors += CheckException("duplicate declaration ${el.container.name}")
            scope.symbols[el.container.name] = (el.value as Literal).value
        }
        // check redundant values
        if (el is IfStatement) when (val ifEl = el.condition) {
            is Equals -> {
                if (scope.symbols[(ifEl.lhs as Container).name] == (ifEl.rhs as Literal).value) {
                    errors += CheckException("redundant value check ${ifEl.lhs.name} is always ${ifEl.rhs}")
                }
            }
            is NotEquals -> {
                if (scope.symbols[(ifEl.lhs as Container).name] != (ifEl.rhs as Literal).value) {
                    errors += CheckException("redundant value check ${ifEl.lhs.name} is always ${ifEl.rhs}")
                }
            }
        }
    }

    return errors
}

data class CheckException(override val message: String) : Exception(message)

class Scope {
    val symbols = mutableMapOf<String, Any>()
}
//
// class CallStack {
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
// }

// fun interpret(l: Lexer) {
//    val stack = CallStack()
//    while (l.hasNext()) {
//        when (val it = doParse(l)) {
//            is VarEntity -> stack.addThing(it.container)
//            is Assignment -> stack[it.lhs.name] = eval(it.rhs)
//        }
//    }
// }
//
// fun eval(e: Expression<*>): Any {
//    when (e) {
//        is
//    }
// }
//
// fun doParse(l: Lexer): Entity = when (val t = l.next()) {
//    Var -> parseVar(l)
//    is Container -> parseContainer(l, t)
//    is TrueLiteral -> t
//    in Builtins ->
//    else -> TODO("parsed some cringe $t")
// }
//
// val Builtins = mapOf(
//    ("print") to { it: Any -> println(it) },
// )
//
