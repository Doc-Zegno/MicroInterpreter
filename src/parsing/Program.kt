package parsing


data class Program(
    val definitions: List<FunctionDefinition>,
    val body: Expression
)
