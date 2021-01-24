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
    IfStatement(startParsing() as Expression<BooleanType>, parseBlock())

/**
 * Expression [(WS+, Infix, Dot) Expression]
 */
fun Lexer.parseExpression() = startParsing() as Expression<*>

@ExperimentalStdlibApi
fun Lexer.parseModule() = Module(/*eww*/buildList { while (hasNext()) add(startParsing()) })

fun Lexer.startParsing(): Entity =
    when (val t = next()) {
        is Expression<*> -> parseIfHasNext(t, ::parseInfix)
        is Container -> parseInfix(t)
        is Entity -> t
        TrueToken -> parseInfix(Literal(true))
        FalseToken -> parseInfix(Literal(false))
        VarToken -> parseContainerDeclaration()
        IfToken -> this.parseIf()
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
        LeftBrace, RightBrace, NewlineToken -> currentEntity // theres no infix to parse
        else -> TODO("continued parsing some cringe: $t, current: $currentEntity")
    }
}

/**
 * Var Container [Assign Expression] NL
 */
fun Lexer.parseContainerDeclaration(): ContainerDeclaration {
    val c = next() as Container
    if (peek() != Assign) return ContainerDeclaration(c)
    next() // Assign
    return ContainerDeclaration(c, parseExpression())
}
