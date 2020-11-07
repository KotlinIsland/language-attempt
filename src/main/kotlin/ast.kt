import kotlin.reflect.KClass

interface Type

interface Entity
interface ModuleLevelEntity

class Module(val statements: List<Entity /*Statement*/>): Entity
fun parseModule(l: Lexer): Module {
    return Module(l.map {
        parseModuleLevelEntity()
    })
}

data class Assignment(val lhs: Container, val rhs: Expression): Entity
fun parseAssignment(l: Lexer) = parseExpression(l)
data class Container(val name: String): Entity
fun parseContainer(l: Lexer) {
    val name = l.next()
    Assignment(name, l)
}

class CompilerVariable(val name: String, val staticType: KClass<Type>, var valueType: KClass<Type>, var value: Type)

/**
 *
 *  1 + 2 + 3
 *  Plus(Plus(1, 2), 3)
 */
data class Expression(val content: Entity): Entity
fun parseExpression(l: Lexer): Entity? {
    val it = when(l.next()) {
        "true" -> BooleanType(true)
        else -> null
    }
    // next can be: nl
    return it
}

data class BooleanType(val value: Boolean): Entity, Type
