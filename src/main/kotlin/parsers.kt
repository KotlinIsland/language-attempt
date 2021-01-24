/**
 * { Entity* }
 */
fun parseBlock(l: Lexer): Block {
    val entries = ArrayList<Entity>()
    while (l.peek() != RightBrace) {
        entries += startParsing(l)
    }
    l.next() // RightBrace
    return Block(entries)
}

/**
 * If Expression Block
 */
fun parseIf(l: Lexer) =
    IfStatement(startParsing(l) as Expression<BooleanType>, parseBlock(l))

/**
 * Expression:
 * 1 [(WS+, Infix, Dot) Expression]
 */
fun parseExpression(l: Lexer) = startParsing(l) as Expression<*>

@ExperimentalStdlibApi
fun parseModule(l: Lexer) = Module(/*eww*/buildList { while (l.hasNext()) add(startParsing(l)) })

fun startParsing(l: Lexer): Entity =
    when (val t = l.next()) {
        is Expression<*> -> if (l.hasNext()) parseInfix(l, t) else t
        is Container -> parseInfix(l, t)
        is Entity -> t
        VarToken -> parseContainerDeclaration(l)
        IfToken -> parseIf(l)
        else -> TODO("started parsing some cringe $t")
    }

fun parseInfix(l: Lexer, currentEntity: Entity) =
    // TODO: what if the next token is something unrelated on a new line?
    when (val t = l.next()) {
        PlusToken -> (currentEntity as Expression<*>) Plus parseExpression(l)
        Assign -> (currentEntity as Container) Assignment parseExpression(l)
        EqualsToken -> (currentEntity as Expression<*>) Equals parseExpression(l)
        else -> TODO("continued parsing some cringe: $t, current: $currentEntity")
    }

fun parseContainerDeclaration(l: Lexer): ContainerDeclaration {
    val c = l.next() as Container
    if (l.peek() != Assign) return ContainerDeclaration(c)
    l.next() // Assign
    return ContainerDeclaration(c, parseExpression(l))
}
