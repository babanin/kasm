package katartal.model

data class FieldAccess(val opcode: Int) {
    companion object {
        val PUBLIC = FieldAccess(0x0001)
        val PRIVATE = FieldAccess(0x0002)
        val PROTECTED = FieldAccess(0x0004)
    }
}
