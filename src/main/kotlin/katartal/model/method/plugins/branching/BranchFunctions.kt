package katartal.model.method.plugins.branching

import katartal.model.ByteCode
import katartal.model.method.CodeBuilder
import katartal.model.method.instruction.InstructionBuilder


fun CodeBuilder._if(code: ByteCode, subRoutine: CodeBuilder.() -> Unit): List<InstructionBuilder> {
    val ifItself: UByte = (1u + 2u).toUByte()

    val codeBuilder = CodeBuilder(
        initialOffset = (currentPos + ifItself).toUShort(),
        constantPool = constantPool,
        labels = labels,
        variables = variables
    )
    codeBuilder.subRoutine()

    val codeLength = codeBuilder.size

    val ifInst = _instruction(code) {
        _indexU2((ifItself + codeLength).toUShort())
    }

    this.plus(codeBuilder)

    val inst = mutableListOf<InstructionBuilder>()
    inst += ifInst
    inst += codeBuilder.instructions
    return inst
}