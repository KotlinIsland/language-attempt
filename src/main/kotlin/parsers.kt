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
 * Expression:
 * 1 [(WS+, Infix, Dot) Expression]
 */
fun Lexer.parseExpression() = startParsing() as Expression<*>

@ExperimentalStdlibApi
fun Lexer.parseModule() = Module(/*eww*/buildList { while (hasNext()) add(startParsing()) })

fun Lexer.startParsing(): Entity =
    when (val t = next()) {
        is Expression<*> -> if (hasNext()) parseInfix(t) else t
        is Container -> parseInfix(t)
        is Entity -> t
        VarToken -> parseContainerDeclaration()
        IfToken -> this.parseIf()
        else -> TODO("started parsing some cringe $t")
    }

fun Lexer.parseInfix(currentEntity: Entity) =
    // TODO: what if the next token is something unrelated on a new line?
    when (val t = next()) {
        PlusToken -> (currentEntity as Expression<*>) Plus parseExpression()
        Assign -> (currentEntity as Container) Assignment parseExpression()
        EqualsToken -> (currentEntity as Expression<*>) Equals parseExpression()
        else -> TODO("continued parsing some cringe: $t, current: $currentEntity")
    }

fun Lexer.parseContainerDeclaration(): ContainerDeclaration {
    val c = next() as Container
    if (peek() != Assign) return ContainerDeclaration(c)
    next() // Assign
    return ContainerDeclaration(c, parseExpression())
}
