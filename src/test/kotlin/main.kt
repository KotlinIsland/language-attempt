import kotlin.test.Test
import kotlin.test.assertEquals

class MainTests {
    @Test
    fun `lex works`() = assertEquals(
        listOf(VarToken, Container("a"), Assign, TrueLiteral),
        Lexer("var a = true").toList()
    )

    @ExperimentalStdlibApi
    @Test
    fun `parse works`() =
        assertEquals(
            listOf(ContainerDeclaration(Container("a"), TrueLiteral)),
            parseModule(Lexer("var a = true")).statements
        )

    @Test
    fun `compile works`() =
        assertEquals(
            "let a = true",
            ContainerDeclaration(Container("a"), TrueLiteral)
                .compile()
        )

    @Test
    fun `check works`() {
        check(listOf(Container("a") Assignment TrueLiteral))
    }


    @Test
    fun `end to end`() =
        assertEquals(
            "let a = true",
            parse(Lexer("var a = true")).joinToString("\n") { it.compile() }
        )

    @Test
    fun `e2e if statement`() =
        assertEquals(
            "if (a === true) {}",
            parse(Lexer("if a == true { }")).joinToString("\n") { it.compile() }
        )
}