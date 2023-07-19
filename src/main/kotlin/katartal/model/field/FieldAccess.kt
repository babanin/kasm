package katartal.model.field

data class FieldAccess(val opcode: UShort) {
    companion object {
        val PUBLIC = FieldAccess(0x0001u)
        val PRIVATE = FieldAccess(0x0002u)
        val PROTECTED = FieldAccess(0x0004u)
        val STATIC = FieldAccess(0x0008u)
        val FINAL = FieldAccess(0x0010u)
        val VOLATILE = FieldAccess(0x0040u)
        val TRANSIENT = FieldAccess(0x0080u)
        val SYNTHETIC = FieldAccess(0x1000u)
        val ENUM = FieldAccess(0x4000u)
    }

    operator fun plus(other: FieldAccess): FieldAccess {
        return FieldAccess((opcode + other.opcode).toUShort())
    }

    operator fun get(access: FieldAccess): Boolean {
        return (this.opcode and access.opcode) != 0.toUShort()
    }
}
