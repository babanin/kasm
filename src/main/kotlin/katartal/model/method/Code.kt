package katartal.model.method

data class Code(val maxStack: UShort, 
                val maxLocals: UShort, 
                val instructions: List<Instruction>, 
                val frames: ) {
    val size: UShort by lazy { instructions.fold(0) { acc, ints -> acc + 1 + ints.operands.size }.toUShort() }
}