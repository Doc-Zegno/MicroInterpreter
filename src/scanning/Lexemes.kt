package scanning


sealed class Lexeme


data class LiteralLexeme(val value: Int) : Lexeme()


data class IdentifierLexeme(val text: String) : Lexeme()


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


data class OperationLexeme(val operationType: OperationType) : Lexeme()


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
}


data class ControlLexeme(val controlType: ControlType) : Lexeme()
