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

    val name: String
        get() = constantPool.readUtf8(nameCpIndex)!!

    val nameCpIndex: UShort
    
    var descriptorCpIndex: UShort
    
    val attributes: MutableList<Attribute> = mutableListOf()

    private val throws = mutableListOf<String>()
    private var parametersDescriptor: String
    private val localsBuilder: LocalsBuilder
    private val codeBuilders: MutableList<CodeBuilder> = mutableListOf()

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
        val codeBuilder =
            if (codeBuilders.isEmpty()) _code { _return() }
            else codeBuilders.reduce { acc, codeBuilder -> acc + codeBuilder }

        attributes += buildCodeAttribute(codeBuilder)
        attributes += buildLocalVariableTable(codeBuilder)
        attributes += buildStackMapFrameTable(codeBuilder)
    }

    private fun buildStackMapFrameTable(codeBuilder: CodeBuilder): StackMapTableAttribute {
        val entries = mutableListOf<StackMapFrameAttribute>()

        fun mapTypes(list: List<StackFrameBuilder.Type>): List<VerificationTypeInfo> {
            return list.map {
                when (it) {
                    is StackFrameBuilder.ObjectVar -> Object_variable_info(CPoolIndex(constantPool.writeClass(it.cls)))
                    is StackFrameBuilder.IntegerVar -> Integer_variable_info()
                    is StackFrameBuilder.TopVar -> Top_variable_info()
                }
            }
        }

        var lastPosition: UShort = 0u
        for (frame in codeBuilder.frames) {
            val offsetDelta = (frame.absoluteOffset - lastPosition).toUShort()

            entries += when (frame) {
                is StackFrameBuilder.AppendFrame -> append_frame(offsetDelta, mapTypes(frame.locals))
                is StackFrameBuilder.FullFrame -> full_frame(offsetDelta, mapTypes(frame.locals), mapTypes(frame.stacks))
                is StackFrameBuilder.ChopFrame -> chop_frame(offsetDelta, frame.k)
                is StackFrameBuilder.SameFrame -> same_frame(offsetDelta.toUByte())
            }

            /*
             The bytecode offset at which a frame applies is calculated by adding offset_delta + 1 to the bytecode 
             offset of the previous frame, unless the previous frame is the initial frame of the method, in which case 
             the bytecode offset is offset_delta.
             https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.4
             */
            lastPosition = (frame.absoluteOffset + 1u).toUShort()
        }

        println("StackMapTable:")
        entries.forEach {
            println("\tframe_type=${it.frameType} $it")
        }
        println()

        return StackMapTableAttribute(constantPool.writeUtf8("StackMapTable"), entries)
    }

    private fun buildCodeAttribute(codeBuilder: CodeBuilder): CodeAttribute {
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

    private fun buildLocalVariableTable(codeBuilder: CodeBuilder): LocalVariableTable {
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

    fun _locals(init: LocalsBuilder.() -> Unit) {
        localsBuilder.init()
    }
}