package katartal.model

import katartal.util.DynamicByteArray
import katartal.util.descriptor

class MethodBuilder(
    name: String = "<init>",
    var access: MethodAccess = MethodAccess.PUBLIC,
    val ctr: Boolean = false,
    val parameters: List<Pair<String, Any>> = listOf(),
    private val constantPool: ConstantPool
) {
    val throws = mutableListOf<String>()

    val name: String
        get() = constantPool.readUtf8(nameCpIndex)!!

    val nameCpIndex: UShort
    var descriptorCpIndex: UShort

    var parametersDescriptor: String

    val codeBuilders: MutableList<CodeBuilder> = mutableListOf()
    val attributes: MutableList<Attribute> = mutableListOf()

    init {
        nameCpIndex = constantPool.writeUtf8(name)

        parametersDescriptor =
            parameters
                .map { it.second }
                .map {
                    when (it) {
                        is Class<*> -> it.descriptor()
                        else -> it.toString()
                    }
                }.joinToString(";", "(", ";)")

        descriptorCpIndex = constantPool.writeUtf8("${parametersDescriptor}V")
    }

    fun <T> Class<T>.path(): String {
        val pkg = this.`package`
        return pkg.name.replace(".", "/") + "/" + this.simpleName
    }

    fun _code(init: CodeBuilder.() -> Unit): CodeBuilder {
        val codeBuilder = CodeBuilder(maxLocals = parameters.size + 1, maxStack = 0, constantPool)
        codeBuilders += codeBuilder
        codeBuilder.init()
        return codeBuilder
    }

    infix fun returns(returnCls: String): MethodBuilder {
        descriptorCpIndex = constantPool.writeUtf8("${parametersDescriptor}$returnCls")
        return this
    }

    infix fun <T : Any> returns(returnCls: Class<T>): MethodBuilder {
        return returns(returnCls.descriptor())
    }

    infix fun throws(interfaceCls: String): MethodBuilder {
        this.throws += interfaceCls
        return this
    }

    infix fun <T : Any> throws(interfaceCls: Class<T>): MethodBuilder {
        this.throws += interfaceCls.path()
        return this
    }

    fun flush() {
        val codeBuilder =
            if (codeBuilders.isEmpty()) _code { _return() }
            else codeBuilders.reduce { acc, codeBuilder -> acc + codeBuilder }

        val attributeNameIndex = constantPool.writeUtf8("Code")

        val codeArray = DynamicByteArray()
        for (instruction in codeBuilder.instructions) {
            codeArray.putU1(instruction.code.opcode)
            for (operand in instruction.operands) {
                codeArray.putU1(operand)
            }
        }

        attributes += CodeAttribute(
            attributeNameIndex,
            codeBuilder.maxStack.toUShort(),
            (1 + parameters.size + codeBuilder.maxLocals).toUShort(),
            codeArray.toByteArray()
        )
    }
}