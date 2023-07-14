package katartal.model

class InstructionBuilder(val code: ByteCode) {     
    val operands = mutableListOf<UByte>()
    
    
    fun _operand(cls: Class<*>) {
        
    }
    
    fun _operand(str: String) {
        
    }
    
    fun _reference(idx : UShort) {
        operands += (idx.toInt() shr 8 and 255).toUByte()
        operands += (idx.toInt() and 255).toUByte()
    }    
    
}