package scanning

import java.lang.Exception
import java.lang.StringBuilder


class ScannerException(message: String) : Exception(message)


class Scanner(lines: List<String>) {
    private var currentLineNumber: Int = 1
    private var currentCharacter: Char? = null

    private val inputIterator: Iterator<Char>
    private val operations2types = mapOf(
        '+' to OperationType.ADD,
        '-' to OperationType.SUBTRACT,
        '*' to OperationType.MULTIPLY,
        '/' to OperationType.DIVIDE,
        '%' to OperationType.MODULO,
        '<' to OperationType.LESS,
        '>' to OperationType.GREATER,
        '=' to OperationType.EQUAL
    )
    private val controls2types = mapOf(
        ',' to ControlType.COMMA,
        ':' to ControlType.COLON,
        '(' to ControlType.OPEN_PARENTHESIS,
        ')' to ControlType.CLOSE_PARENTHESIS,
        '[' to ControlType.OPEN_BRACKET,
        ']' to ControlType.CLOSE_BRACKET,
        '{' to ControlType.OPEN_BRACE,
        '}' to ControlType.CLOSE_BRACE,
        '?' to ControlType.QUESTION,
        '\n' to ControlType.EOL
    )


    init {
        val sequence = lines.joinToSequence()
        inputIterator = sequence.iterator()
        moveNextCharacter()
    }


    fun getNextLexeme(): Lexeme? {
        if (currentCharacter != null) {
            val current = currentCharacter!!
            moveNextCharacter()

            // Identifier
            if (current.isValidLetter()) {
                return scanIdentifier(current)
            }

            // Literal
            if (current.isDigit()) {
                return scanLiteral(current)
            }

            // Operation
            val operationType = operations2types[current]
            if (operationType != null) {
                return OperationLexeme(operationType, currentLineNumber)
            }

            // Control
            val controlType = controls2types[current]
            if (controlType != null) {
                return ControlLexeme(controlType, currentLineNumber)
            }

            // Unknown character
            raiseError("Unknown character: '$current'")
        } else {
            return null
        }
    }


    private fun scanLiteral(first: Char): Lexeme {
        var value = first - '0'

        while (currentCharacter != null) {
            val current = currentCharacter!!
            if (current.isDigit()) {
                value *= 10
                value += current - '0'
                moveNextCharacter()
            } else {
                break
            }
        }

        return LiteralLexeme(value, currentLineNumber)
    }


    private fun scanIdentifier(first: Char): Lexeme {
        val builder = StringBuilder()
        builder.append(first)

        while (currentCharacter != null) {
            val current = currentCharacter!!
            if (current.isValidLetter()) {
                builder.append(current)
                moveNextCharacter()
            } else {
                break
            }
        }

        return IdentifierLexeme(builder.toString(), currentLineNumber)
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


    private fun raiseError(message: String): Nothing {
        throw ScannerException(message)
    }


    companion object {
        private fun Char.isValidLetter(): Boolean = this in 'A'..'Z' || this in 'a'..'z' || this == '_'
    }
}