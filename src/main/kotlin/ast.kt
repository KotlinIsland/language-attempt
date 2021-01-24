import kotlin.reflect.KClass

interface Type
class BooleanType : Type
interface Entity {
    fun compile(): String
}

interface ModuleLevelEntity

data class Module(val statements: List<Entity /*Module Entity*/>) : Entity {
    override fun compile() = statements.joinToString("\n") { it.compile() }
}

// Tokens
// The universe of tokens consists of singletons { keywords, operators, whitespace } and
//  group values like Ints { 1, 2, 3 ... } and symbols { foo, bar ... }
interface Token // SUS: i think tokens should be separated from entities

object PlusToken : Token
object Assign : Token
object LeftBrace : Token
object RightBrace : Token
object EqualsToken : Token
object NotEqualsToken : Token
object LeftParenthesis : Token
object RightParenthesis : Token
object LeftBracket : Token
object RightBracket : Token
object IfToken : Token
object VarToken : Token
object TrueToken : Token
object FalseToken : Token

// NamedEntity
// alphanumeric | underscore, can't be just underscores, can't start with a number
interface NamedEntity : Entity, Token {
    val name: String
}

// TODO make plus a BinaryOperator
interface BinaryOperator
enum class BinaryOperators

//  constructor
infix fun Expression<*>.Plus(rhs: Expression<*>) = Plus<Type>(this, rhs)
data class Plus<R : Type>(val lhs: Expression<*>, val rhs: Expression<*>) : Expression<R> {
    override fun toString() = "$lhs Plus $rhs"
    override fun compile() = lhs.compile() + " + " + rhs.compile()
}

data class Function(override val name: String /*params, code*/) : NamedEntity {
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

data class Container(override val name: String) : Expression<Type>, NamedEntity {
    override fun compile() = name
}

interface Expression<out T : Type> : Entity
data class Literal(val value: Any) : Expression<Type> {
    override fun compile() = value.toString()
}
infix fun Expression<*>.Equals(rhs: Expression<*>) = Equals(this, rhs)
data class Equals(val lhs: Expression<*>, val rhs: Expression<*>) : Expression<BooleanType> {
    override fun toString() = "$lhs Equals $rhs"
    override fun compile() = "${lhs.compile()} === ${rhs.compile()}"
}

infix fun Expression<*>.NotEquals(rhs: Expression<*>) = NotEquals(this, rhs)
data class NotEquals(val lhs: Expression<*>, val rhs: Expression<*>) : Expression<BooleanType> {
    override fun toString() = "$lhs NotEquals $rhs"
    override fun compile() = "${lhs.compile()} !== ${rhs.compile()}"
}

class Block(val body: List<Entity>) : Entity {
    override fun compile() = body.joinToString("\n") { it.compile() }
}

class IfStatement(val condition: Expression<BooleanType>, val ifTrue: Block) : Entity {
    override fun compile() = "if(${condition.compile()}) {${ifTrue.compile()}}"
}

class CompilerVariable(val name: String, val staticType: KClass<Type>, var valueType: KClass<Type>, var value: Type)
