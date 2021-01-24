import kotlin.test.Test
import kotlin.test.assertEquals

class MainTests {
    @Test
    fun `lex works`() = assertEquals(listOf(Container("a"), Assign, TrueLiteral), Lexer("a = true").toList())

    @ExperimentalStdlibApi
    @Test
    fun `parse works`() =
        assertEquals(listOf(ContainerDeclaration(Container("a"), TrueLiteral)), parseModule(Lexer("var a = true")).statements)

    @Test
    fun `compile works`() {
        compile(Container("a") Assignment TrueLiteral)
    }

    @Test
    fun `check works`() {
        check(listOf(Container("a") Assignment TrueLiteral))
    }

    @Test
    /**
     * a = true
     * if a {}
     */
    fun `negative check`(): Unit = TODO(
        """something like
        assertEqual(
            listOf(CompileError("condition always true")),
            check(listOf(Assignment(Container("a"), BooleanType(true)), If(Container("a", EmptyBlock))))
        )
        """
    )

//    @Test
//    fun `end to end`() =
//        assertEquals("var a = true\nif (a) {\nconsole.log(1)\n}", main("a = true\nif a == true {\nprint(1)\n}"))
}