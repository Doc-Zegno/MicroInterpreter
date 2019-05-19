package parsing

import scanning.OperationType


sealed class Expression {
    abstract val line: Int
}


data class ConstantExpression(
    val value: Int,
    override val line: Int
) : Expression()


data class BinaryExpression(
    val operationType: OperationType,
    val left: Expression,
    val right: Expression,
    override val line: Int
) : Expression()


data class IfExpression(
    val condition: Expression,
    val ifBranch: Expression,
    val elseBranch: Expression,
    override val line: Int
) : Expression()


data class IdentifierExpression(
    val name: String,
    val identifierNumber: Int,
    override val line: Int
) : Expression()


data class CallExpression(
    val name: String,
    val functionNumber: Int,
    val arguments: List<Expression>,
    override val line: Int
) : Expression()
