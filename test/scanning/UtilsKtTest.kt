package scanning

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*


internal class UtilsKtTest {
    @Test
    fun `joinToSequence on empty list`() {
        val lines = listOf<String>()
        val expected = listOf<Char>()
        val actual = lines.joinToSequence().toList()
        assertEquals(expected, actual)
    }


    @Test
    fun `joinToSequence on one-liner`() {
        val lines = listOf("Sample text")
        val expected = "Sample text".asSequence().toList()
        val actual = lines.joinToSequence().toList()
        assertEquals(expected, actual)
    }


    @Test
    fun `joinToSequence on many lines`() {
        val lines = listOf("Sample text", "Serious Arguments", "Hello World")
        val expected = "Sample text\nSerious Arguments\nHello World".asSequence().toList()
        val actual = lines.joinToSequence().toList()
        assertEquals(expected, actual)
    }
}