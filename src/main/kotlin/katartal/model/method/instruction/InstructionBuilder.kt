package katartal.model.method.instruction

import katartal.model.ByteCode
import katartal.model.CPoolIndex
import katartal.model.method.Instruction
import katartal.model.method.InstructionContainer

sealed class InstructionBuilder(val code: ByteCode) : InstructionContainer() {
    override fun instructions(): List<InstructionBuilder> = listOf(this)

    val operands = mutableListOf<UByte>()

    fun _atype(type: UByte) {
        operands += type
    }

    fun _position(position: Short) {
        operands += (position.toInt() shr 8 and 255).toUByte()
        operands += (position.toInt() and 255).toUByte()
    }

    fun _indexU1(idx: UShort) {
        operands += (idx.toInt() and 255).toUByte()
    }

    fun _indexU1(idx: CPoolIndex) {
        operands += (idx.toInt() and 255).toUByte()
    }

    fun _indexU2(idx: UShort) {
        operands += (idx.toInt() shr 8 and 255).toUByte()
        operands += (idx.toInt() and 255).toUByte()
    }

    fun _indexU2(idx: CPoolIndex) {
        operands += (idx.toInt() shr 8 and 255).toUByte()
        operands += (idx.toInt() and 255).toUByte()
    }

    fun _index(idx: UByte) {
        operands += idx
    }

    fun _const(num: Byte) {
        operands += num.toUByte()
    }

    fun _value(num: Short) {
        operands += (num.toInt() shr 8 and 255).toUByte()
        operands += (num.toInt() and 255).toUByte()
    }

    open val size: UShort
        get() = (1 + operands.size).toUShort()

    abstract fun flush(): Instruction
    
    override fun toString(): String = "InstructionBuilder(code=$code, operands=$operands)"

    companion object {
        fun eager(code: ByteCode): EagerInstructionBuilder {
            return EagerInstructionBuilder(code)
        }

        fun lazy(code: ByteCode, reserve: UShort, evaluator: LazyInstructionBuilder.() -> Unit): LazyInstructionBuilder {
            return LazyInstructionBuilder(code, reserve, evaluator)
        }
    }
}