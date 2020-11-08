import com.sun.source.tree.ContinueTree
import kotlin.reflect.KClass

interface Type
class BooleanType : Type
interface Entity
interface ModuleLevelEntity

data class Module(val statements: List<Entity /*Statement*/>) : Entity

// Tokens
// The universe of tokens consists of singletons { keywords, operators, whitespace } and
//  group values like Ints { 1, 2, 3 ... } and symbols { foo, bar ... }
interface Token
object TrueLiteral : Expression<BooleanType>, Token
object FalseLiteral : Expression<BooleanType>, Token
object PlusToken: Token
object Assign : Token

data class IntLiteral(val value: Int) : Entity, Token

// NamedEntity
// alphanumeric | underscore, can't be just underscores, can't start with a number
interface NamedEntity : Entity, Token {
    val name: String
}

// TODO make plus a BinaryOperator
interface BinaryOperator
enum class BinaryOperators { }

data class Plus(val lhs: Expression<*>, val rhs: Expression<*>) : Entity {
    override fun toString() = "$lhs Plus $rhs"
}

infix fun Expression<*>.Plus(rhs: Expression<*>) = Plus(this, rhs)

data class Assignment(val lhs: Container, val rhs: Expression<*>) : Entity
infix fun Container.Assignment(rhs: Expression<*>) = Assignment(this, rhs)

data class Container(override val name: String) : NamedEntity


/**
 *
 *  1 + 2 + 3
 *  Plus(Plus(1, 2), 3)
 */
interface Expression<out T : Type> : Entity

fun parseExpression(l: Lexer) = startParsing(l) as Expression<*>

// broken, because startParsing calls next but so does map
fun parseModule(l: Lexer) = Module(l.map { startParsing(l) })


fun startParsing(l: Lexer): Entity =
    when (val t = l.next()) {
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

fun parseContainer(l: Lexer, currentExpression: NamedEntity) = Container(currentExpression.name)

fun parseAssignment(l: Lexer, currentExpression: Container) =
    currentExpression Assignment parseExpression(l)

class CompilerVariable(val name: String, val staticType: KClass<Type>, var valueType: KClass<Type>, var value: Type)
