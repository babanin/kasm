package katartal.model.cls

data class ClassAccess(val opcode: UShort) {
    companion object {
        val PUBLIC = ClassAccess(0x0001u)
        val PRIVATE = ClassAccess(0x0002u)
        val PROTECTED = ClassAccess(0x0004u)
        val FINAL = ClassAccess(0x0010u)
        val SUPER = ClassAccess(0x0020u)
        val INTERFACE = ClassAccess(0x0200u)
        val ABSTRACT = ClassAccess(0x0400u)
        val SYNTHETIC = ClassAccess(0x1000u)
        val ANNOTATION = ClassAccess(0x2000u)
        val ENUM = ClassAccess(0x4000u)
        val MODULE = ClassAccess(0x8000u)
    }

    operator fun plus(access: ClassAccess): ClassAccess {
        return ClassAccess(this.opcode or access.opcode)
    }
}
