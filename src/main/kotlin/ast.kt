import kotlin.reflect.KClass

class Assignment(val lhs: Container, val rhs: Any)

class Container(val name: String)

class CompilerVariable(val name: String, val staticType: KClass<Type>, var valueType: KClass<Type>, var value: Type)

open class Type

class BooleanType(val value: Boolean): Type()
