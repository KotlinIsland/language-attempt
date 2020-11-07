import kotlin.test.Test
import kotlin.test.assertEquals

class MainTests {
    @Test
    fun `lex works`() = assertEquals(listOf("a", "=", "true"), buildList {
        val l = Lexer("a = true")
        while (true) when (val n = l.next()) {
            null -> break
            else -> add(n)
        }
    })

    @Test
    fun `parse works`() =
        assertEquals(listOf(Assignment(Container("a"), BooleanType(true))), parseModule(Lexer("a = true")).statements)

    @Test
    fun `compile works`() {
        compile(listOf(Assignment(Container("a"), BooleanType(true))))
    }

    @Test
    fun `check works`() {
        check(listOf(Assignment(Container("a"), BooleanType(true))))
    }

    @Test
    fun `negative check`(): Unit = TODO(
        """something like
        assertEqual(
            listOf(CompileError("condition always true")),
            check(listOf(Assignment(Container("a"), BooleanType(true)), If(Container("a", EmptyBlock))))
        )
        """
    )

    @Test
    fun `end to end`() =
        assertEquals("var a = true\nif (a) {\nconsole.log(1)\n}", main("a = true\nif a == true {\nprint(1)\n}"))
}