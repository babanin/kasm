package katartal.model

import katartal.util.path
import kotlin.math.max

class CodeBuilder(
    var maxLocals: Int = -1,
    var maxStack: Int = -1,
    private val constantPool: ConstantPool
) {
    val instructions = mutableListOf<InstructionBuilder>()

    private fun ensureStackCapacity(minStackSize: Int) {
        maxStack = max(maxStack, minStackSize)
    }

    fun _return(boolean: Boolean) {
        ensureStackCapacity(1)

        _instruction(if (boolean) ByteCode.ICONST_1 else ByteCode.ICONST_0)
        _instruction(ByteCode.IRETURN)
    }

    fun _return(): InstructionBuilder {
        return _instruction(ByteCode.RETURN)
    }

    fun _invokeSpecial(cls: Class<*>, method: String, description: String): List<InstructionBuilder> {
        ensureStackCapacity(1)
        
        return listOf(
            _instruction(ByteCode.ALOAD_0),
            _instruction(ByteCode.INVOKESPECIAL) {
                _reference(constantPool.writeMethodRef(cls.path(), method, description))
            }
        )
    }

    fun _instruction(code: ByteCode, init: InstructionBuilder.() -> Unit): InstructionBuilder {
        val builder = InstructionBuilder(code)
        builder.init()
        instructions.add(builder)
        return builder
    }

    fun _instruction(code: ByteCode): InstructionBuilder {
        val builder = InstructionBuilder(code)
        instructions.add(builder)
        return builder
    }

    fun _nop(): InstructionBuilder {
        return _instruction(ByteCode.NOP)
    }

    operator fun plus(other: CodeBuilder): CodeBuilder {
        this.instructions += other.instructions
        this.maxStack = max(maxStack, other.maxStack)
        this.maxLocals = max(maxLocals, other.maxLocals)
        return this
    }
}