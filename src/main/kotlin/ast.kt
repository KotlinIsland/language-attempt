import kotlin.reflect.KClass

interface Type
class BooleanType : Type
interface Entity {
    fun compile(): String
}

interface ModuleLevelEntity

data class Module(val statements: List<Entity /*Statement*/>) : Entity {
    override fun compile() = statements.joinToString("\n") { it.compile() }
}

// Tokens
// The universe of tokens consists of singletons { keywords, operators, whitespace } and
//  group values like Ints { 1, 2, 3 ... } and symbols { foo, bar ... }
interface Token
object TrueLiteral : Expression<BooleanType>, Token {
    override fun compile() = "true"
}

object FalseLiteral : Expression<BooleanType>, Token {
    override fun compile() = "false"
}

object PlusToken : Token
object Assign : Token
object EqualsToken : Token
object IfToken : Token
object VarToken : Token
data class IntLiteral(val value: Int) : Entity, Token {
    override fun compile() = value.toString()
}

// NamedEntity
// alphanumeric | underscore, can't be just underscores, can't start with a number
interface NamedEntity : Entity, Token {
    val name: String
}

// TODO make plus a BinaryOperator
interface BinaryOperator
enum class BinaryOperators {}

//  constructor
infix fun Expression<*>.Plus(rhs: Expression<*>) = Plus<Type>(this, rhs)
data class Plus<R : Type>(val lhs: Expression<*>, val rhs: Expression<*>) : Expression<R> {
    override fun toString() = "$lhs Plus $rhs"
    override fun compile() = lhs.compile() + " + " + rhs.compile()
}

data class Function(override val name: String /*params, code*/): NamedEntity {
    override fun compile() = "fun $name() { }"
}

data class ContainerDeclaration(val container: Container, val value: Expression<*>? = null) : Entity {
    override fun compile() = "let ${container.compile()}" + if (value != null) " = " + value.compile() else ""
}

// constructor
infix fun Container.Assignment(rhs: Expression<*>) = Assignment(this, rhs)
data class Assignment(val lhs: Container, val rhs: Expression<*>) : Entity {
    override fun compile() = "${lhs.compile()} = ${rhs.compile()}"
}

data class Container(override val name: String) : NamedEntity {
    override fun compile() = name
}

interface Expression<out T : Type> : Entity
interface Literal<out T : Type> : Expression<T>

infix fun Expression<*>.Equals(rhs: Expression<*>) = Equals(this, rhs)
data class Equals(val lhs: Expression<*>, val rhs: Expression<*>) : Expression<BooleanType> {
    override fun toString() = "$lhs Equals $rhs"
    override fun compile() = "${lhs.compile()} === ${rhs.compile()}"
}

class Block(val body: List<Entity>) : Entity  {
    override fun compile() = body.joinToString("\n") { it.compile() }
}

class IfStatement(val condition: Expression<BooleanType>, val ifTrue: Block) : Entity {
    override fun compile() = "if(${condition.compile()}) {${ifTrue.compile()}}"
}

/**
 * { Entity* }
 */
fun parseBlock(l: Lexer): Block {
    val entries = ArrayList<Entity>()
    while (l.hasNext()) {
        entries += startParsing(l)
    }
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
        is Expression<*> -> if (l.hasNext()) continueParsing(l, t) else t
        is Container -> if (l.hasNext())
            continueParsing(l, t)
        else
            error("FAIL!!!! THERE NEEDS TO BE AN EXPRESSION HERE")
        is Entity -> t
        VarToken -> parseContainerDeclaration(l)
        IfToken -> parseIf(l)
        else -> TODO("started parsing some cringe $t")
    }

fun continueParsing(l: Lexer, currentEntity: Entity) =
    // TODO: what if the next token is something unrelated on a new line?
    when (val t = l.next()) {
        PlusToken -> (currentEntity as Expression<*>) Plus parseExpression(l)
        Assign -> (currentEntity as Container) Assignment parseExpression(l)
        EqualsToken -> (currentEntity as Expression<*>) Equals parseExpression(l)
        else -> TODO("continues parsing some cringe $t")
    }

fun parseContainerDeclaration(l: Lexer): ContainerDeclaration {
    val c = l.next() as Container
    if (l.peek() != Assign) return ContainerDeclaration(c)
    l.next() // Assign
    return ContainerDeclaration(c, parseExpression(l))
}

class CompilerVariable(val name: String, val staticType: KClass<Type>, var valueType: KClass<Type>, var value: Type)
