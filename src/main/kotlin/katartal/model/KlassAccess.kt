package katartal.model

data class KlassAccess(val opcode: Int) {
    companion object {
        val PUBLIC = KlassAccess(0x0001)
        val PRIVATE = KlassAccess(0x0002)
        val PROTECTED = KlassAccess(0x0004)
    }
}
