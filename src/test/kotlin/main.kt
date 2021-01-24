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
            parse(Lexer("var a = true"))
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
    fun `e2e ContainerDeclaration`() =
        assertEquals(
            "let a = true",
            compile(parse(Lexer("var a = true")))
        )

    @Test
    fun `e2e IfStatement`() =
        assertEquals(
            "if(a === true) {}",
            compile(parse(Lexer("if a == true { }")))
        )
}
