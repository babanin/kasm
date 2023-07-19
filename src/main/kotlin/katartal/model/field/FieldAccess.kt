package katartal.model.field

data class FieldAccess(val opcode: Int) {
    companion object {
        val PUBLIC = FieldAccess(0x0001)
        val PRIVATE = FieldAccess(0x0002)
        val PROTECTED = FieldAccess(0x0004)
        val STATIC = FieldAccess(0x0008)
        val FINAL = FieldAccess(0x0010)
        val VOLATILE = FieldAccess(0x0040)
        val TRANSIENT = FieldAccess(0x0080)
        val SYNTHETIC = FieldAccess(0x1000)
        val ENUM = FieldAccess(0x4000)
    }
    
    operator fun plus(other: FieldAccess) : FieldAccess {
        return FieldAccess(opcode + other.opcode)
    }
}
