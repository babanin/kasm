package katartal.model.method

import katartal.model.ByteCode

class InstructionBuilder(val code: ByteCode) {
    val operands = mutableListOf<UByte>()


    fun _operand(cls: Class<*>) {

    }

    fun _operand(str: String) {

    }

    fun _referenceU1(idx: UShort) {
        operands += (idx.toInt() and 255).toUByte()
    }

    fun _referenceU2(idx: UShort) {
        operands += (idx.toInt() shr 8 and 255).toUByte()
        operands += (idx.toInt() and 255).toUByte()
    }

    override fun toString(): String {
        return "InstructionBuilder(code=$code, operands=$operands)"
    }


}