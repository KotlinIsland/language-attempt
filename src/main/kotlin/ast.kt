import kotlin.reflect.KClass

interface Type
class BooleanType : Type

interface Entity {
    //these should go somewhere, but maybe not here, if the AST is a representation of the code and not the literal code
    //itself, the start/end points wouldnt be relevant, only when compiling and parsing would they be relevant
    val start: Int
    val end: Int

    //move this? i dont think compile goes in the entites, i think the AST should just be a representation of the
    // code, and the compiling gets done elsewhere? but idk
    abstract fun compile(): String
}

interface ModuleLevelEntity

data class Module(
    val statements: List<Entity /*Statement*/>
) : Entity {
    override val start = statements.first().start
    override val end = statements.last().end
    override fun compile() = statements.joinToString("\n") { it.compile() }
}

// Tokens
// The universe of tokens consists of singletons { keywords, operators, whitespace } and
//  group values like Ints { 1, 2, 3 ... } and symbols { foo, bar ... }
interface Token
class TrueLiteral(override val start: Int, override val end: Int) : Expression<BooleanType>, Token {
    override fun compile() = "true"
}

class FalseLiteral(override val start: Int, override val end: Int) : Expression<BooleanType>, Token {
    override fun compile() = "false"
}

object PlusToken : Token
object Assign : Token
object Var : Token
data class IntLiteral(override val start: Int, override val end: Int, val value: Int) : Entity, Token {
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


//is this even needed or cringe?
class LeftAndRightEntity(val lhs: Entity, val rhs: Entity): Entity {
    override val start = lhs.start
    override val end = rhs.end
    override fun compile() = TODO("idk")
}

data class Plus(val lhs: Expression<*>, val rhs: Expression<*>) : Entity by LeftAndRightEntity(lhs, rhs) {
    override fun toString() = "$lhs Plus $rhs"
    override fun compile() = lhs.compile() + " + " + rhs.compile()
}

data class Function(
    override val start: Int,
    override val end: Int,
    override val name: String /*params, code*/
) : NamedEntity {
    override fun compile() = "fun $name() { }"
}

infix fun Expression<*>.Plus(rhs: Expression<*>) = Plus(this, rhs)

data class Assignment(val lhs: Container, val rhs: Expression<*>) : Entity by LeftAndRightEntity(lhs, rhs) {
    override fun compile() = "${lhs.compile()} = ${rhs.compile()}"
}

infix fun Container.Assignment(rhs: Expression<*>) = Assignment(this, rhs)

data class Container(override val start: Int, override val end: Int, override val name: String) : NamedEntity {
    override fun compile() = name
}

interface Expression<out T : Type> : Entity

fun parseExpression(l: Lexer) = startParsing(l) as Expression<*>

@ExperimentalStdlibApi
fun parseModule(l: Lexer) = Module(/*eww*/buildList { while (l.hasNext()) add(startParsing(l)) })


fun startParsing(l: Lexer): Entity =
    when (val t = l.next()) {
        is Entity -> t
        is TrueLiteral -> continueParsing(l, TrueLiteral(l.pos, l.pos + t.))
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








































