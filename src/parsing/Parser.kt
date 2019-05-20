package parsing

import scanning.*
import java.lang.Exception


class SyntaxException() : Exception("SYNTAX ERROR")
class ParameterNotFoundException(name: String, line: Int) : Exception("PARAMETER NOT FOUND $name:$line")
class FunctionNotFoundException(name: String, line: Int) : Exception("FUNCTION NOT FOUND $name:$line")
class ArgumentNumberMismatch(name: String, line: Int) : Exception("ARGUMENT NUMBER MISMATCH $name:$line")


class Parser(lines: List<String>) {
    private val scannedLines: List<List<Lexeme>>

    private val functionSignatures = mutableListOf<FunctionSignature>()
    private val functionalScope = mutableMapOf<String, Int>()
    private val variableScope = mutableMapOf<String, Int>()

    private var currentLineNumber: Int = 1
    private var currentLexeme: Lexeme? = null
    private var currentLexemeNumber: Int = -1


    init {
        val lists = mutableListOf<List<Lexeme>>()

        for (line in lines) {
            val scanner = Scanner(line)
            val list = mutableListOf<Lexeme>()
            while (true) {
                val lexeme = scanner.getNextLexeme()
                if (lexeme != null) {
                    list.add(lexeme)
                } else {
                    break
                }
            }
            lists.add(list)
        }

        scannedLines = lists
    }


    fun parse(): Program {
        if (scannedLines.isEmpty()) {
            throw SyntaxException()
        }

        // 1st pass: collect functions' signatures
        reset()
        moveNextLexeme()
        functionSignatures.clear()
        functionSignatures.addAll(parseFunctionSignatureList())
        functionalScope.clear()
        for ((index, signature) in functionSignatures.withIndex()) {
            if (functionalScope.containsKey(signature.name)) {
                throw SyntaxException()
            }
            functionalScope[signature.name] = index
        }

        // 2nd pass: fully parse all functions and expressions
        reset()
        moveNextLexeme()
        val definitions = mutableListOf<FunctionDefinition>()
        for (signature in functionSignatures) {
            definitions.add(parseFunctionDefinition(signature))
            moveNextLine()
        }
        variableScope.clear()
        val programBody = parseExpression()
        checkEndOfLine()

        return Program(definitions, programBody)
    }


    // <expression> ::= <identifier>
    //                  | <constant-expression>
    //                  | <binary-expression>
    //                  | <if-expression>
    //                  | <call-expression>
    private fun parseExpression(): Expression {
        val current = currentLexeme
        return when (current) {
            is OperationLexeme -> parseUnaryExpression()
            is ControlLexeme -> when (current.controlType) {
                ControlType.OPEN_PARENTHESIS -> parseBinaryExpression()
                ControlType.OPEN_BRACKET -> parseIfExpression()
                else -> throw SyntaxException()
            }
            is IdentifierLexeme -> parseIdentifierOrCallExpression()
            is LiteralLexeme -> parseLiteralExpression()
            else -> throw SyntaxException()
        }
    }


    // <number>
    private fun parseLiteralExpression(): Expression {
        val value = getLiteralValue()
        return ConstantExpression(value, currentLineNumber)
    }


    // <constant-expression> ::= "-" <number> | <number>
    private fun parseUnaryExpression(): Expression {
        checkOperationType(OperationType.SUBTRACT)
        val rightValue = getLiteralValue()
        return ConstantExpression(-rightValue, currentLineNumber)
    }


    // <binary-expression> ::= "(" <expression> <operation> <expression>  ")"
    private fun parseBinaryExpression(): Expression {
        checkControlType(ControlType.OPEN_PARENTHESIS)
        val left = parseExpression()
        val operationType = getOperationType()
        val right = parseExpression()
        checkControlType(ControlType.CLOSE_PARENTHESIS)
        return BinaryExpression(operationType, left, right, currentLineNumber)
    }


    // <if-expression> ::= "[" <expression> "]?{" <expression> "}:{"<expression>"}"
    private fun parseIfExpression(): Expression {
        // Condition
        checkControlType(ControlType.OPEN_BRACKET)
        val condition = parseExpression()
        checkControlType(ControlType.CLOSE_BRACKET)

        // If
        checkControlType(ControlType.QUESTION)
        checkControlType(ControlType.OPEN_BRACE)
        val ifBranch = parseExpression()
        checkControlType(ControlType.CLOSE_BRACE)

        // Else
        checkControlType(ControlType.COLON)
        checkControlType(ControlType.OPEN_BRACE)
        val elseBranch = parseExpression()
        checkControlType(ControlType.CLOSE_BRACE)

        return IfExpression(condition, ifBranch, elseBranch, currentLineNumber)
    }


    // <identifier> | <identifier> "(" <argument-list> ")"
    private fun parseIdentifierOrCallExpression(): Expression {
        val name = getIdentifierText()
        if (whetherControlType(ControlType.OPEN_PARENTHESIS)) {
            // Function call
            val number = functionalScope[name]
            if (number != null) {
                val signature = functionSignatures[number]
                val arguments = parseFunctionArguments()
                if (arguments.count() != signature.argumentNames.count()) {
                    throw ArgumentNumberMismatch(name, currentLineNumber)
                }
                return CallExpression(name, number, arguments, currentLineNumber)
            } else {
                throw FunctionNotFoundException(name, currentLineNumber)
            }
        } else {
            // Just identifier
            val number = variableScope[name]
            if (number != null) {
                return IdentifierExpression(name, number, currentLineNumber)
            } else {
                throw ParameterNotFoundException(name, currentLineNumber)
            }
        }
    }


    // "(" <argument-list> ")"
    private fun parseFunctionArguments(): List<Expression> {
        val arguments = mutableListOf<Expression>()
        checkControlType(ControlType.OPEN_PARENTHESIS)

        if (!whetherControlType(ControlType.CLOSE_PARENTHESIS)) {
            arguments.add(parseExpression())
            while (!whetherControlType(ControlType.CLOSE_PARENTHESIS)) {
                checkControlType(ControlType.COMMA)
                arguments.add(parseExpression())
            }
        }

        checkControlType(ControlType.CLOSE_PARENTHESIS)
        return arguments
    }


    // <identifier>"(" <parameter_list> ")" "={" <expression> "}"
    private fun parseFunctionDefinition(signature: FunctionSignature): FunctionDefinition {
        while (!whetherOperationType(OperationType.EQUAL)) {
            if (!moveNextLexeme()) {
                throw SyntaxException()
            }
        }
        checkOperationType(OperationType.EQUAL)
        checkControlType(ControlType.OPEN_BRACE)

        // Clear scope and place all arguments onto stack
        variableScope.clear()
        for ((index, name) in signature.argumentNames.withIndex()) {
            if (variableScope.containsKey(name)) {
                throw SyntaxException()
            }
            variableScope[name] = index
        }

        // Parse function's body
        val body = parseExpression()

        checkControlType(ControlType.CLOSE_BRACE)
        checkEndOfLine()

        return FunctionDefinition(signature, body)
    }


    // list of <identifier>"(" <parameter_list> ")"
    private fun parseFunctionSignatureList(): List<FunctionSignature> {
        val signatures = mutableListOf<FunctionSignature>()

        // Function functionSignatures are placed on every line except the last
        val lastLineNumber = scannedLines.count()
        while (currentLineNumber < lastLineNumber) {
            signatures.add(parseFunctionSignature())
            moveNextLine()
        }

        return signatures
    }


    // <identifier>"(" <parameter_list> ")"
    private fun parseFunctionSignature(): FunctionSignature {
        val name = getIdentifierText()

        checkControlType(ControlType.OPEN_PARENTHESIS)
        val argumentNames = mutableListOf<String>()
        if (!whetherControlType(ControlType.CLOSE_PARENTHESIS)) {
            argumentNames.add(getIdentifierText())
            while (!whetherControlType(ControlType.CLOSE_PARENTHESIS)) {
                checkControlType(ControlType.COMMA)
                argumentNames.add(getIdentifierText())
            }
        }
        checkControlType(ControlType.CLOSE_PARENTHESIS)

        return FunctionSignature(name, argumentNames)
    }


    private fun getIdentifierText(): String {
        val current = currentLexeme
        if (current is IdentifierLexeme) {
            moveNextLexeme()
            return current.text
        } else {
            throw SyntaxException()
        }
    }


    private fun getLiteralValue(): Int {
        val current = currentLexeme
        if (current is LiteralLexeme) {
            moveNextLexeme()
            return current.value
        } else {
            throw SyntaxException()
        }
    }


    private fun getOperationType(): OperationType {
        val current = currentLexeme
        if (current is OperationLexeme) {
            moveNextLexeme()
            return current.operationType
        } else {
            throw SyntaxException()
        }
    }


    private fun checkOperationType(operationType: OperationType) {
        if (!whetherOperationType(operationType)) {
            throw SyntaxException()
        } else {
            moveNextLexeme()
        }
    }


    private fun whetherOperationType(operationType: OperationType): Boolean {
        val current = currentLexeme
        return current is OperationLexeme && current.operationType == operationType
    }


    private fun checkControlType(controlType: ControlType) {
        if (!whetherControlType(controlType)) {
            throw SyntaxException()
        } else {
            moveNextLexeme()
        }
    }


    private fun whetherControlType(controlType: ControlType): Boolean {
        val current = currentLexeme
        return current is ControlLexeme && current.controlType == controlType
    }


    private fun checkEndOfLine() {
        if (currentLexeme != null) {
            throw SyntaxException()
        }
    }


    private fun reset() {
        currentLexeme = null
        currentLexemeNumber = -1
        currentLineNumber = 1
    }


    private fun moveNextLine() {
        currentLineNumber++
        currentLexemeNumber = -1
        moveNextLexeme()
    }


    private fun moveNextLexeme(): Boolean {
        currentLexemeNumber++
        val currentLine = scannedLines[currentLineNumber - 1]
        return if (currentLexemeNumber < currentLine.count()) {
            currentLexeme = currentLine[currentLexemeNumber]
            true
        } else {
            currentLexeme = null
            false
        }
    }
}