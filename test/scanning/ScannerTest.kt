package scanning

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


internal class ScannerTest {
    @Test
    fun `getNextLexeme on empty input`() {
        val line = ""
        val scanner = Scanner(line)
        assertNull(scanner.getNextLexeme())
    }


    @Test
    fun `getNextLexeme on identifier`() {
        val expected = "__lessThanOrEqual__"
        val scanner = Scanner(expected)

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
        val line = "Sample,Text"
        val scanner = Scanner(line)

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


    @Test
    fun `getNextLexeme on two literals`() {
        val line = "137+42"
        val scanner = Scanner(line)

        // First lexeme should be IdentifierLexeme
        val lexeme1 = scanner.getNextLexeme()
        assertNotNull(lexeme1)
        assertTrue(lexeme1 is LiteralLexeme)
        assertEquals(137, lexeme1.value)

        // Second one should be a control
        val lexeme2 = scanner.getNextLexeme()
        assertNotNull(lexeme2)
        assertTrue(lexeme2 is OperationLexeme)
        assertEquals(OperationType.ADD, lexeme2.operationType)

        // Third lexeme should be IdentifierLexeme
        val lexeme3 = scanner.getNextLexeme()
        assertNotNull(lexeme3)
        assertTrue(lexeme3 is LiteralLexeme)
        assertEquals(42, lexeme3.value)

        // Final one should be null
        assertNull(scanner.getNextLexeme())
    }


    @Test
    fun `getNextLexeme invalid character`() {
        val line = "&"
        val scanner = Scanner(line)

        assertThrows<ScannerException> { scanner.getNextLexeme() }
    }
}