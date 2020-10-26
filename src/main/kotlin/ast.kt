import kotlin.reflect.KClass

class Module(val statements: List<Any /*Statement*/>)

data class Assignment(val lhs: Container, val rhs: Any)

data class Container(val name: String)

class CompilerVariable(val name: String, val staticType: KClass<Type>, var valueType: KClass<Type>, var value: Type)

open class Type

data class BooleanType(val value: Boolean): Type()
