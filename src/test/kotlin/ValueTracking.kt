import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import kotlin.test.assertEquals

class ValueTracking {
    @Test
    fun `check redundant value`() {
        assertEquals(
            listOf(CheckException("redundant value check a is always Literal(value=true)")),
            check(
                listOf(
                    ContainerDeclaration(Container("a"), Literal(true)),
                    IfStatement(Container("a") Equals Literal(true), Block(emptyList())),
                )
            )
        )
    }

    @Disabled
    @Test
    fun `infer type from exact class`() {
        test(
            """
            open class Base {
                fun foo(): Any = 1
            }
            class Der : Base() {
                override fun foo(): String = "1"
            }
            
            val i = Base().foo() 
            """.trimIndent(),
            "check that i is Int",
        )
    }
}

fun test(vararg a: Any): Nothing = TODO("implement testing stuff")
