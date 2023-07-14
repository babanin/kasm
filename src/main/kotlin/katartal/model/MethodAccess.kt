package katartal.model

data class MethodAccess(val opcode: Int) {
    companion object {
        val PUBLIC = MethodAccess(0x0001)
        val PRIVATE = MethodAccess(0x0002)
        val PROTECTED = MethodAccess(0x0004)
    }
}