package katartal.model.method.plugins.lvt

import katartal.model.method.CodeBuilder
import katartal.model.method.LocalVariable
import katartal.util.descriptor


/**
 * LocalVariableTable
 */
fun CodeBuilder.variable(name: String, descriptor: Class<*>): CodeVariable {
    return variable(name, descriptor.descriptor())
}

fun CodeBuilder.variable(name: String, descriptor: String): CodeVariable {
    return CodeVariable(name, descriptor, currentPos)
}

fun CodeBuilder.releaseVariable(variable: CodeVariable) {
    variables += LocalVariable(
        constantPool.writeUtf8(variable.name),
        variable.startPc,
        (currentPos - variable.startPc).toUShort(),
        constantPool.writeUtf8(variable.descriptor)
    )
}

data class CodeVariable(val name: String, val descriptor: String, val startPc: UShort)
