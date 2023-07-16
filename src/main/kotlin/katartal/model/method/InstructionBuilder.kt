package katartal.model.method

import katartal.model.ByteCode

class InstructionBuilder(val code: ByteCode) {
    val operands = mutableListOf<UByte>()

    fun _atype(type: UByte) {
        operands += type
    }
    
    fun _position(position: UShort) {
        operands += (position.toInt() shr 8 and 255).toUByte()
        operands += (position.toInt() and 255).toUByte()
    }
    
    fun _referenceU1(idx: UShort) {
        operands += (idx.toInt() and 255).toUByte()
    }

    fun _referenceU2(idx: UShort) {
        operands += (idx.toInt() shr 8 and 255).toUByte()
        operands += (idx.toInt() and 255).toUByte()
    }
    
    val size : Int
        get() = 1 + operands.size

    override fun toString(): String {
        return "InstructionBuilder(code=$code, operands=$operands)"
    }
}