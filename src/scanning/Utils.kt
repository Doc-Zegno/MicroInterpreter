package scanning


fun List<String>.joinToSequence(): Sequence<Char> {
    val decorated = mutableListOf<Sequence<Char>>()
    for ((index, line) in this.withIndex()) {
        if (index != 0) {
            decorated.add(sequenceOf('\n'))
        }
        decorated.add(line.asSequence())
    }
    return decorated.asSequence().flatten()
}
