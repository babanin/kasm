package katartal.model

import katartal.util.path

class MethodBuilder(val name: String = "<init>", val ctr: Boolean = false) {
    val throws = mutableListOf<String>()
    val instructions = mutableListOf<InstructionBuilder>()

    fun _return(boolean: Boolean) {
        _instruction(if (boolean) ByteCode.ICONST_1 else ByteCode.ICONST_0)
        _instruction(ByteCode.IRETURN)
    }

    fun invokeSpecial(cls: Class<*>, method: String, description: String): InstructionBuilder {
        return _instruction(ByteCode.INVOKESPECIAL) {
            _operand(cls.path())
            _operand(method)
            _operand(description)
        }
    }

    fun _instruction(code: ByteCode, init: InstructionBuilder.() -> Unit): InstructionBuilder {
        val builder = InstructionBuilder(code)
        builder.init()
        instructions.add(builder)
        return builder
    }

    fun _instruction(code: ByteCode): InstructionBuilder {
        val builder = InstructionBuilder(code)
        instructions.add(builder)
        return builder
    }
    
    fun <T> Class<T>.path(): String {
        val pkg = this.`package`
        return pkg.name.replace(".", "/") + "/" + this.simpleName
    }

    infix fun throws(interfaceCls: String): MethodBuilder {
        this.throws += interfaceCls
        return this
    }

    infix fun <T : Any> throws(interfaceCls: Class<T>): MethodBuilder {
        this.throws += interfaceCls.path()
        return this
    }
}