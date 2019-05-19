package runtime

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import parsing.Parser


internal class InterpreterTest {
    @Test
    fun `execute on single expressions`() {
        assertEquals(4, compileAndExecute(listOf("(2+2)")))
        assertEquals(4, compileAndExecute(listOf("(2+((3*4)/5))")))
        assertEquals(15, compileAndExecute(listOf("(1+(2+(3+(4+5))))")))
        assertEquals(1, compileAndExecute(listOf("(5>4)")))
        assertEquals(0, compileAndExecute(listOf("(5<4)")))
        assertEquals(3, compileAndExecute(listOf("(8%5)")))
    }


    @Test
    fun `execute on conditional`() {
        assertEquals(0, compileAndExecute(listOf("[((10+20)>(20+10))]?{1}:{0}")))
    }


    @Test
    fun `execute on fibonacci`() {
        assertEquals(
            60,
            compileAndExecute(
                listOf(
                    "g(x)={(f(x)+f((x/2)))}",
                    "f(x)={[(x>1)]?{(f((x-1))+f((x-2)))}:{x}}",
                    "g(10)"
                )
            )
        )

        // With tail recursion (how do you like it, Lisp?)
        assertEquals(
            60,
            compileAndExecute(
                listOf(
                    "g(x)={(f(x)+f((x/2)))}",
                    "f(x)={[(x>1)]?{h((x-1),1,0)}:{x}}",
                    "h(n,y,z)={[n]?{h((n-1),(y+z),y)}:{y}}",
                    "g(10)"
                )
            )
        )
    }


    @Test
    fun `execute on runtime error`() {
        assertThrows<InterpreterException> {
            compileAndExecute(
                listOf(
                    "f(x,y,z)={0}",
                    "g(x,y,z)={1}",
                    "(g(1,2,3)/f(4,5,g(6,7,8)))"
                )
            )
        }
    }


    companion object {
        private fun compileAndExecute(lines: List<String>): Int {
            val program = Parser(lines).parse()
            return Interpreter(program).execute()
        }
    }
}