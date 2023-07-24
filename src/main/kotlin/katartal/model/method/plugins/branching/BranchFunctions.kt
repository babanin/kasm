package katartal.model.method.plugins.branching

import katartal.model.ByteCode
import katartal.model.method.CodeBuilder
import katartal.model.method.instruction.InstructionBuilder
import katartal.util.descriptor


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

data class ExceptionLabel(val exception: String, val handlerLabel: String) {
    constructor(exception: Class<*>, handlerLabel: String) : this(exception.descriptor(), handlerLabel)
}

infix fun String.handledBy(label: String): ExceptionLabel {
    return ExceptionLabel(this, label)
}

infix fun <T> Class<T>.handledBy(label: String): ExceptionLabel {
    return ExceptionLabel(this, label)
}

fun CodeBuilder._tryCatch(vararg handlers: ExceptionLabel, block: CodeBuilder.() -> Unit): List<InstructionBuilder> {
    val codeBuilder = CodeBuilder(
        initialOffset = currentPos,
        constantPool = constantPool,
        labels = labels,
        variables = variables
    )

    codeBuilder.block()

    this.plus(codeBuilder)

    for (handler in handlers) {
        exceptionHandlers += CodeBuilder.ExceptionHandler(
            currentPos,
            currentPos +,
            0u,
            constantPool.writeClass(handler.exception)
        )
    }

    val inst = mutableListOf<InstructionBuilder>()
    inst += codeBuilder.instructions
    return inst
}