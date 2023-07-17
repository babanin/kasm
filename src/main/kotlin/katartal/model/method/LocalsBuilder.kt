package katartal.model.method

import katartal.model.ConstantPool
import katartal.util.descriptor

class LocalsBuilder(val constantPool: ConstantPool) {
    val variables = mutableListOf<LocalVariable>()

    data class LocalVariable(
        val nameIndex: UShort,
        val startPc: UShort,
        val length: UShort,
        val descriptor: UShort
    )

    fun _var(name: String, descriptor: Class<*>, startPc: UShort, length: UShort): LocalVariable {
        return _var(name, descriptor.descriptor(), startPc, length)
    }
    
    fun _var(name: String, descriptor: String, startPc: UShort, length: UShort): LocalVariable {
        val localVariable = LocalVariable(constantPool.writeUtf8(name), startPc, length, constantPool.writeUtf8(descriptor))
        variables += localVariable

        return localVariable
    }

}