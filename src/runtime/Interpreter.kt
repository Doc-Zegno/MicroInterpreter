package runtime

import parsing.*
import scanning.OperationType
import java.lang.Exception
import java.util.*


class InterpreterException(expression: String, line: Int) : Exception("RUNTIME ERROR $expression:$line")


class Interpreter(private val program: Program) {
    private val scopeStack: ArrayDeque<IntArray> = ArrayDeque()  // IntArray for performance only


    fun execute(): Int {
        return evaluate(program.body)
    }


    private fun evaluate(expression: Expression): Int {
        return when (expression) {
            is ConstantExpression -> expression.value
            is BinaryExpression -> evaluateBinaryExpression(expression)
            is IfExpression -> evaluateIfExpression(expression)
            is IdentifierExpression -> evaluateIdentifierExpression(expression)
            is CallExpression -> evaluateCallExpression(expression)
        }
    }


    private fun evaluateCallExpression(expression: CallExpression): Int {
        // Evaluate arguments and push onto stack
        val arguments = IntArray(expression.arguments.count()) {
            i -> evaluate(expression.arguments[i])
        }
        scopeStack.addLast(arguments)

        // Fetch function definition and execute
        val number = expression.functionNumber
        val definition = program.definitions[number]
        val result = evaluate(definition.body)

        // Remove last stack frame
        scopeStack.removeLast()
        return result
    }


    private fun evaluateIdentifierExpression(expression: IdentifierExpression): Int {
        val scope = scopeStack.peekLast()
        return scope[expression.identifierNumber]
    }


    private fun evaluateIfExpression(expression: IfExpression): Int {
        return if (evaluate(expression.condition) != 0) {
            evaluate(expression.ifBranch)
        } else {
            evaluate(expression.elseBranch)
        }
    }


    private fun evaluateBinaryExpression(expression: BinaryExpression): Int {
        val left = evaluate(expression.left)
        val right = evaluate(expression.right)

        return when (expression.operationType) {
            OperationType.ADD -> left + right
            OperationType.SUBTRACT -> left - right
            OperationType.MULTIPLY -> left * right
            OperationType.DIVIDE -> evaluateDivision(expression, left, right)
            OperationType.MODULO -> left % right
            OperationType.GREATER -> if (left > right) 1 else 0
            OperationType.LESS -> if (left < right) 1 else 0
            OperationType.EQUAL -> if (left == right) 1 else 0
        }
    }


    private fun evaluateDivision(expression: BinaryExpression, left: Int, right: Int): Int {
        if (right != 0) {
            return left / right
        } else {
            throw InterpreterException(expression.toCodeString(), expression.line)
        }
    }


    companion object {
        private fun Expression.toCodeString(): String {
            return when (this) {
                is ConstantExpression -> this.value.toString()
                is BinaryExpression -> this.toCodeString()
                is IfExpression -> this.toCodeString()
                is IdentifierExpression -> this.name
                is CallExpression -> this.toCodeString()
            }
        }


        private fun CallExpression.toCodeString(): String {
            val arguments = this.arguments.map { it.toCodeString() }
            return "${this.name}(${arguments.joinToString(",")})"
        }


        private fun IfExpression.toCodeString(): String {
            val condition = this.condition.toCodeString()
            val ifBranch = this.ifBranch.toCodeString()
            val elseBranch = this.elseBranch.toCodeString()
            return "[$condition]?{$ifBranch}:{$elseBranch}"
        }


        private fun BinaryExpression.toCodeString(): String {
            val left = this.left.toCodeString()
            val right = this.right.toCodeString()
            val operation = this.operationType.toCodeString()
            return "($left$operation$right)"
        }


        private fun OperationType.toCodeString(): String {
            return when (this) {
                OperationType.ADD -> "+"
                OperationType.SUBTRACT -> "-"
                OperationType.MULTIPLY -> "*"
                OperationType.DIVIDE -> "/"
                OperationType.MODULO -> "%"
                OperationType.GREATER -> ">"
                OperationType.LESS -> "<"
                OperationType.EQUAL -> "="
            }
        }
    }
}