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
object Var : Token
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

data class Plus(val lhs: Expression<*>, val rhs: Expression<*>) : Entity {
    override fun toString() = "$lhs Plus $rhs"
    override fun compile() = lhs.compile() + " + " + rhs.compile()
}

data class Function(override val name: String /*params, code*/): NamedEntity {
    override fun compile() = "fun $name() { }"
}

infix fun Expression<*>.Plus(rhs: Expression<*>) = Plus(this, rhs)

data class Assignment(val lhs: Container, val rhs: Expression<*>) : Entity {
    override fun compile() = "${lhs.compile()} = ${rhs.compile()}"
}

infix fun Container.Assignment(rhs: Expression<*>) = Assignment(this, rhs)

data class Container(override val name: String) : NamedEntity {
    override fun compile() = name
}

interface Expression<out T : Type> : Entity

fun parseExpression(l: Lexer) = startParsing(l) as Expression<*>

@ExperimentalStdlibApi
fun parseModule(l: Lexer) = Module(/*eww*/buildList { while (l.hasNext()) add(startParsing(l)) })


fun startParsing(l: Lexer): Entity =
    when (val t = l.next()) {
        is Entity -> t
        TrueLiteral -> continueParsing(l, TrueLiteral)
        else -> TODO("started parsing some cringe $t")
    }


fun continueParsing(l: Lexer, currentEntity: Entity) =
    when (val t = l.next()) {
        PlusToken -> parsePlus(l, currentEntity as Expression<*>)
        Assign -> parseAssignment(l, currentEntity as Container)
        else -> TODO("continues parsing come cringe $t")
    }


fun parsePlus(l: Lexer, currentExpression: Expression<*>) =
    currentExpression Plus parseExpression(l)

fun parseContainer(l: Lexer, currentExpression: Container): Assignment {
    val op = l.next()
    check(op is Assign /* or dot or plus ..., maybe make this property an interface*/)
    return currentExpression Assignment parseExpression(l)
}

fun parseAssignment(l: Lexer, currentExpression: Container) =
    currentExpression Assignment parseExpression(l)

class CompilerVariable(val name: String, val staticType: KClass<Type>, var valueType: KClass<Type>, var value: Type)

/**
 * just messing around with interpreter
 */
fun parseVar(l: Lexer): VarEntity {
    val (c, v) = parseContainer(l, l.next() as Container)
    return VarEntity(c, v)
}

data class VarEntity(val container: Container, val value: Expression<*>) : Entity /*, Assignment */ {
    override fun compile() = "var ${container.compile()} = ${value.compile()}"
}








































