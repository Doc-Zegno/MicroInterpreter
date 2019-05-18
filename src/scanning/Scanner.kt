package scanning

import java.lang.IllegalArgumentException
import java.lang.StringBuilder


class Scanner(lines: List<String>) {
    private var currentLineNumber: Int = 1
    private var currentCharacter: Char? = null

    private val inputIterator: Iterator<Char>


    init {
        val sequence = lines.joinToSequence()
        inputIterator = sequence.iterator()
        moveNextCharacter()
    }


    fun getNextLexeme(): Lexeme? {
        if (currentCharacter != null) {
            val current = currentCharacter!!

            // Identifier
            if (isLetter(current)) {
                return scanIdentifier(current)
            }

            // TODO: add other options
            // Control
            return scanControl(current)
        } else {
            return null
        }
    }


    private fun scanControl(current: Char): Lexeme {
        val controlType = getControlType(current)
        moveNextCharacter()
        return ControlLexeme(controlType, currentLineNumber)
    }


    private fun getControlType(current: Char): ControlType {
        return when (current) {
            ',' -> ControlType.COMMA
            ':' -> ControlType.COLON

            // TODO: replace with ScannerException
            else -> throw IllegalArgumentException("Unknown control character")
        }
    }


    private fun scanIdentifier(first: Char): Lexeme {
        val builder = StringBuilder()
        builder.append(first)

        while (moveNextCharacter()) {
            val current = currentCharacter!!
            if (isLetter(current)) {
                builder.append(current)
            } else {
                break
            }
        }

        return IdentifierLexeme(builder.toString(), currentLineNumber)
    }


    private fun isLetter(char: Char): Boolean {
        return char in 'A'..'Z' || char in 'a'..'z' || char == '_'
    }


    private fun moveNextCharacter(): Boolean {
        return if (inputIterator.hasNext()) {
            currentCharacter = inputIterator.next()
            if (currentCharacter == '\n') {
                currentLineNumber++
            }
            true
        } else {
            currentCharacter = null
            false
        }
    }
}