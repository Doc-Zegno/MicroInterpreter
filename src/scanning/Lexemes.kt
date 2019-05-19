package scanning


sealed class Lexeme {
    abstract val line: Int
}


data class LiteralLexeme(val value: Int, override val line: Int) : Lexeme()


data class IdentifierLexeme(val text: String, override val line: Int) : Lexeme()


enum class OperationType {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MODULO,
    GREATER,
    LESS,
    EQUAL,
}


data class OperationLexeme(val operationType: OperationType, override val line: Int) : Lexeme()


enum class ControlType {
    OPEN_PARENTHESIS,
    CLOSE_PARENTHESIS,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    OPEN_BRACE,
    CLOSE_BRACE,
    QUESTION,
    COLON,
    COMMA,
    EOL,
}


data class ControlLexeme(val controlType: ControlType, override val line: Int) : Lexeme()
