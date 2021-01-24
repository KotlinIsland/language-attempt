import kotlin.test.Test
import kotlin.test.assertEquals

class MainTests {
    @Test
    fun `lex works`() = assertEquals(
        listOf(VarToken, ContainerToken("a"), Assign, TrueToken),
        Lexer("var a = true").toList()
    )

    @Test
    fun `parse works`() =
        assertEquals(
            listOf(ContainerDeclaration(Container("a"), Literal(true))),
            parse(Lexer("var a = true"))
        )

    @Test
    fun `parse module`() =
        assertEquals(
            Module(listOf(ContainerDeclaration(Container("a")))),
            Lexer("var a").parseModule()
        )

    @Test
    fun `compile works`() =
        assertEquals(
            "let a = true",
            ContainerDeclaration(Container("a"), Literal(true))
                .compile()
        )

    @Test
    fun `check works`() {
        check(listOf(Container("a") Assignment Literal(true)))
    }

    @Test
    fun `check for duplicate declaration`() {
        assertEquals(
            listOf(CheckException("duplicate declaration a")),
            check(
                listOf(
                    ContainerDeclaration(Container("a"), Literal(true)),
                    ContainerDeclaration(Container("a"), Literal(true)),
                )
            )
        )
    }

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

    @Test
    fun `infix expression`() =
        assertEquals(
            "1 === 1",
            compile(parse(Lexer("1 == 1")))
        )

    @Test
    fun `newline before infix`() =
        assertEquals(
            "1 === 1",
            compile(parse(Lexer("1 \n== 1")))
        )

    @Test
    fun `newline after infix`() =
        assertEquals(
            "1 === 1",
            compile(parse(Lexer("1 ==\n 1")))
        )
}
