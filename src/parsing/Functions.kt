package parsing


data class FunctionSignature(
    val name: String,
    val argumentNames: List<String>
)


data class FunctionDefinition(
    val signature: FunctionSignature,
    val body: Expression
)
