package demo

import parsing.Parser
import runtime.Interpreter
import java.lang.Exception


fun main() {
    try {
        val lines = mutableListOf<String>()
        while (true) {
            val line = readLine()
            if (line != null) {
                lines.add(line)
            } else {
                break
            }
        }
        val program = Parser(lines).parse()
        print(Interpreter(program).execute())
    } catch (e: Exception) {
        print(e.message)
    }
}
