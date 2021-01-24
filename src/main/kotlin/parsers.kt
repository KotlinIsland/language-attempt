/**
 * { Entity* }
 */
fun Lexer.parseBlock(): Block {
    val entries = ArrayList<Entity>()
    while (peek() != RightBrace) {
        entries += startParsing()
    }
    next() // RightBrace
    return Block(entries)
}

/**
 * If Expression Block
 */
fun Lexer.parseIf() =
    // TODO: maybe Expression needs to have a property specifying its return type so the generic doesnt get deleted at runtime
    IfStatement(parseExpression() as Expression<BooleanType>, parseBlock())

/**
 * Expression [(WS+, Infix, Dot) Expression]
 */
fun Lexer.parseExpression() = startParsing() as Expression<*>

fun Lexer.parseModule() = Module(map(::startParsing))

fun Lexer.startParsing(t: Token = next()): Entity = when (t) {
    TrueToken -> parseInfix(Literal(true))
    FalseToken -> parseInfix(Literal(false))
    // sus: these are literals as well, why dont they also use Literal?
    is ContainerToken -> parseInfix(Container(t.name))
    is IntLiteralToken -> parseInfix(Literal(t.value))
    VarToken -> parseContainerDeclaration()
    IfToken -> parseIf()
    else -> TODO("started parsing some cringe $t")
}

fun Lexer.parseInfix(currentEntity: Entity): Entity {
    // TODO: what if the next token is something unrelated on a new line?
    return when (
        val t = try {
            next()
        } catch (e: Exception) {
            println(e)
            return currentEntity
        }
    ) {
        PlusToken -> (currentEntity as Expression<*>) Plus parseExpression()
        Assign -> (currentEntity as Container) Assignment parseExpression()
        EqualsToken -> (currentEntity as Expression<*>) Equals parseExpression()
        NotEqualsToken -> (currentEntity as Expression<*>) NotEquals parseExpression()
        LeftBrace, RightBrace, NewlineToken -> currentEntity // theres no infix to parse
        else -> TODO("continued parsing some cringe: $t, current: $currentEntity")
    }
}

/**
 * Var Container [Assign Expression] NL
 */
fun Lexer.parseContainerDeclaration(): ContainerDeclaration {
    val c = Container((next() as ContainerToken).name)
    if (peek() != Assign) return ContainerDeclaration(Container(c.name))
    next() // Assign
    return ContainerDeclaration(c, parseExpression())
}
