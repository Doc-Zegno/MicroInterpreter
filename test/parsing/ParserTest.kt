package parsing

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows


internal class ParserTest {
    @Test
    fun `parse on empty input`() {
        assertThrows<SyntaxException> {
            val lines = listOf<String>()
            Parser(lines).parse()
        }
    }


    @Test
    fun `parse on invalid syntax`() {
        assertThrows<SyntaxException> {
            // No surrounding parenthesis
            val lines = listOf("2+2")
            Parser(lines).parse()
        }

        assertThrows<SyntaxException> {
            // No function body
            val lines = listOf(
                "g(x)",
                "g(10)"
            )
            Parser(lines).parse()
        }

        assertThrows<SyntaxException> {
            // First line is not a function signature
            val lines = listOf(
                "g(10)",
                "g(10)"
            )
            Parser(lines).parse()
        }

        assertThrows<SyntaxException> {
            // Wrong if syntax (double ?)
            val lines = listOf("[0]?{1}?{0}")
            Parser(lines).parse()
        }
    }


    @Test
    fun `parse on parameter not found`() {
        assertThrows<ParameterNotFoundException> {
            // Can you spot it?
            val lines = listOf(
                "calculate_square(num_rows,num_cols)={(num_row*num_cols)}",
                "calculate_square(3,4)"
            )
            Parser(lines).parse()
        }
    }


    @Test
    fun `parse on function not found`() {
        assertThrows<FunctionNotFoundException> {
            // Can you spot it?
            val lines = listOf(
                "get_imdb_rating(film_number)={9}",
                "get_film_number()={7}",
                "get_imbd_rating(get_film_number())"
            )
            Parser(lines).parse()
        }
    }


    @Test
    fun `parse on argument number mismatch`() {
        assertThrows<ArgumentNumberMismatch> {
            // Can you spot it?
            val lines = listOf(
                "foo(a,b,c,d,e,f,g,h)={137}",
                "bar(a,b,c,d,e,f,g,h)={foo(a,b,c,d,e,f,h)}",
                "bar(1,2,3,4,5,6,7,8)"
            )
            Parser(lines).parse()
        }
    }
}
