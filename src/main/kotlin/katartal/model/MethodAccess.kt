package katartal.model

data class MethodAccess(val opcode: UShort) {
    companion object {
        val PUBLIC = MethodAccess(0x0001u)
        val PRIVATE = MethodAccess(0x0002u)
        val PROTECTED = MethodAccess(0x0004u)
        val STATIC = MethodAccess(0x0008u)
        val FINAL = MethodAccess(0x0010u)
        val SYNCHRONIZED = MethodAccess(0x0020u)
        val BRIDGE = MethodAccess(0x0040u)
        val VARARGS = MethodAccess(0x0080u)
        val NATIVE = MethodAccess(0x0100u)
        val ABSTRACT = MethodAccess(0x0400u)
        val STRICT = MethodAccess(0x0800u)
        val SYNTHETIC = MethodAccess(0x1000u)
    }
}