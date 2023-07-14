package katartal.model

open class Access(val opcode: Int) {
    fun plus(access: Access): Access {
        return Access(opcode + access.opcode)
    }

    companion object {
        val STATIC = Access(
            0x0008 // field, method
        )
        val FINAL = Access(
            0x0010 // class, field, method, parameter
        )
//        val SUPER = Access(Opcodes.ACC_SUPER)
//        val SYNCHRONIZED = Access(Opcodes.ACC_SYNCHRONIZED)
//        val OPEN = Access(Opcodes.ACC_OPEN)
//        val TRANSITIVE = Access(Opcodes.ACC_TRANSITIVE)
//        val VOLATILE = Access(Opcodes.ACC_VOLATILE)
//        val BRIDGE = Access(Opcodes.ACC_BRIDGE)
//        val STATIC_PHASE = Access(Opcodes.ACC_STATIC_PHASE)
//        val VARARGS = Access(Opcodes.ACC_VARARGS)
//        val TRANSIENT = Access(Opcodes.ACC_TRANSIENT)
//        val NATIVE = Access(Opcodes.ACC_NATIVE)
//        val INTERFACE = Access(Opcodes.ACC_INTERFACE)
//        val ABSTRACT = Access(Opcodes.ACC_ABSTRACT)
//        val STRICT = Access(Opcodes.ACC_STRICT)
//        val SYNTHETIC = Access(Opcodes.ACC_SYNTHETIC)
//        val ANNOTATION = Access(Opcodes.ACC_ANNOTATION)
//        val ENUM = Access(Opcodes.ACC_ENUM)
//        val MANDATED = Access(Opcodes.ACC_MANDATED)
//        val MODULE = Access(Opcodes.ACC_MODULE)
    }
}