package scanning

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


internal class ScannerTest {
    @Test
    fun `getNextLexeme on empty input`() {
        val lines = listOf<String>()
        val scanner = Scanner(lines)
        assertNull(scanner.getNextLexeme())
    }


    @Test
    fun `getNextLexeme on identifier`() {
        val expected = "__lessThanOrEqual__"
        val lines = listOf<String>(expected)
        val scanner = Scanner(lines)

        // First lexeme should be IdentifierLexeme
        val lexeme = scanner.getNextLexeme()
        assertNotNull(lexeme)
        assertTrue(lexeme is IdentifierLexeme)
        assertEquals(expected, lexeme.text)

        // Second should be null
        assertNull(scanner.getNextLexeme())
    }


    @Test
    fun `getNextLexeme on two identifiers`() {
        val lines = listOf<String>("Sample,Text")
        val scanner = Scanner(lines)

        // First lexeme should be IdentifierLexeme
        val lexeme1 = scanner.getNextLexeme()
        assertNotNull(lexeme1)
        assertTrue(lexeme1 is IdentifierLexeme)
        assertEquals("Sample", lexeme1.text)

        // Second one should be a control
        val lexeme2 = scanner.getNextLexeme()
        assertNotNull(lexeme2)
        assertTrue(lexeme2 is ControlLexeme)
        assertEquals(ControlType.COMMA, lexeme2.controlType)

        // Third lexeme should be IdentifierLexeme
        val lexeme3 = scanner.getNextLexeme()
        assertNotNull(lexeme3)
        assertTrue(lexeme3 is IdentifierLexeme)
        assertEquals("Text", lexeme3.text)

        // Final one should be null
        assertNull(scanner.getNextLexeme())
    }
}