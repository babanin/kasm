package katartal.model.method

import katartal.model.*
import katartal.util.DynamicByteArray
import katartal.util.descriptor
import katartal.util.path

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

    val localsBuilder: LocalsBuilder
    val codeBuilders: MutableList<CodeBuilder> = mutableListOf()
    val attributes: MutableList<Attribute> = mutableListOf()

    init {
        nameCpIndex = constantPool.writeUtf8(name)

        localsBuilder = LocalsBuilder(constantPool)

        parametersDescriptor =
            parameters
                .map { it.second }
                .joinToString("", "(", ")") {
                    when (it) {
                        is Class<*> -> it.descriptor()
                        else -> it.toString()
                    }
                }

        descriptorCpIndex = constantPool.writeUtf8("${parametersDescriptor}V")
    }


    fun _code(maxLocals: Int = -1, maxStack: Int = -1, init: CodeBuilder.() -> Unit): CodeBuilder {
        val codeBuilder =
            CodeBuilder(
                maxLocals = if (maxLocals == -1) parameters.size + 1 else maxLocals,
                maxStack,
                constantPool = constantPool
            )
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
        attributes += buildCodeAttribute()
        attributes += buildLocalVariableTable()
        attributes += buildStackMapFrameTable()
    }

    private fun buildStackMapFrameTable(): StackMapTableAttribute {
        val entries = mutableListOf<StackMapFrame>()

        entries += append_frame(
            6u,
            listOf(
                Object_variable_info(CPoolIndex(constantPool.writeClass("[I"))),
                Integer_variable_info()
            )
        )
        entries += chop_frame(1u, 14u)

        println("StackMapTable:")
        entries.forEach {
            println("\tframe_type=${it.frameType} $it")
        }
        println()

        return StackMapTableAttribute(constantPool.writeUtf8("StackMapTable"), entries)
    }

    private fun buildCodeAttribute(): CodeAttribute {
        val codeBuilder =
            if (codeBuilders.isEmpty()) _code { _return() }
            else codeBuilders.reduce { acc, codeBuilder -> acc + codeBuilder }

        var position = 0

        println("Code: ")
        val codeArray = DynamicByteArray()
        for (instruction in codeBuilder.instructions) {
            codeArray.putU1(instruction.code.opcode)
            for (operand in instruction.operands) {
                codeArray.putU1(operand)
            }

            println(
                "\t$position\t${instruction.code}\t${
                    instruction.operands.joinToString("\t")
                }"
            )
            position += (1 + instruction.operands.size)
        }
        println()

        return CodeAttribute(
            constantPool.writeUtf8("Code"),
            codeBuilder.maxStack.toUShort(),
            (parameters.size + codeBuilder.maxLocals).toUShort(),
            codeArray.toByteArray()
        )
    }

    private fun buildLocalVariableTable(): LocalVariableTable {
        val codeBuilder =
            if (codeBuilders.isEmpty()) _code { _return() }
            else codeBuilders.reduce { acc, codeBuilder -> acc + codeBuilder }

        val localVarsTable = parameters.mapIndexed { index, pair ->
            LocalVariableTableEntry(
                0u,
                codeBuilder.currentPos,
                constantPool.writeUtf8(pair.first),
                constantPool.writeUtf8(
                    when (pair.second) {
                        is Class<*> -> (pair.second as Class<*>).descriptor()
                        else -> pair.second.toString()
                    }
                ),
                index.toUShort()
            )
        }.toMutableList()

        localVarsTable += localsBuilder.variables.mapIndexed { i, it ->
            LocalVariableTableEntry(
                it.startPc, it.length, it.nameIndex, it.descriptor, (i + localVarsTable.size).toUShort()
            )
        }

        println("LocalVariableTable: ")
        println("\tStart\t\tLength\t\tSlot\t\tName\t\tSignature")
        localVarsTable.forEach {
            println("\t${it.startPc}\t\t\t${it.length}\t\t\t${it.index}\t\t\t${it.nameIndex}\t\t\t${it.descriptorIndex}")
        }
        println()

        return LocalVariableTable(
            constantPool.writeUtf8("LocalVariableTable"),
            localVarsTable
        )
    }

    fun _locals(function: LocalsBuilder.() -> Unit) {
        localsBuilder.function()
    }
}