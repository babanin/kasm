package katartal.model.method.plugins.branching

import katartal.model.ByteCode
import katartal.model.method.CodeBuilder
import katartal.model.method.CodeBuilder.ExceptionHandler
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

data class ExceptionLabel(val exception: String, val handlerLabel: String)

infix fun String.handledBy(label: String): ExceptionLabel {
    return ExceptionLabel(this, label)
}

fun CodeBuilder._tryCatch(handlers: List<ExceptionLabel>, block: CodeBuilder.() -> Unit): List<InstructionBuilder> {
    val codeBuilder = CodeBuilder(
        initialOffset = currentPos,
        constantPool = constantPool,
        labels = labels,
        variables = variables
    )

    codeBuilder.block()
    this.plus(codeBuilder)
    
//    exceptionHandlers += ExceptionHandler(currentPos, currentPos + )

    val inst = mutableListOf<InstructionBuilder>()
    inst += codeBuilder.instructions
    return inst
}