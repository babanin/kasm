package katartal.model.method

import katartal.model.ByteCode
import katartal.model.CPoolIndex
import katartal.model.ConstantPool
import katartal.model.method.instruction.InstructionBuilder
import katartal.model.method.instruction.LazyInstructionBuilder
import katartal.util.descriptor
import katartal.util.max
import katartal.util.path

open class CodeBuilder(
    var maxLocals: UShort = 0u,
    var maxStack: UShort = 0u,
    private val initialOffset: UShort = 0u,
    internal val labels: MutableMap<String, Label>,
    internal val variables: MutableList<LocalVariable> = mutableListOf(),
    internal val constantPool: ConstantPool
) : InstructionContainer() {
    val code = mutableListOf<InstructionContainer>()
    val frames = mutableListOf<StackFrameBuilder.StackFrame>()
    val exceptionHandlers = mutableListOf<ExceptionHandler>()

    private fun ensureStackCapacity(minStackSize: UShort) {
        maxStack = max(maxStack, minStackSize)
    }

    override fun instructions(): List<InstructionBuilder> = code.flatMap { it.instructions() }

    fun _ldc(cpIndex: CPoolIndex): InstructionBuilder {
        if (cpIndex.index > 255u) {
            return _instruction(ByteCode.LDC_W) {
                _indexU2(cpIndex)
            }
        }

        return _instruction(ByteCode.LDC) {
            _indexU1(cpIndex)
        }
    }

    fun _ldc(value: String): InstructionBuilder {
        return _ldc(constantPool.writeString(value))
    }


    fun _ldc(value: Int): InstructionBuilder {
        return _ldc(constantPool.writeInteger(value))
    }

    fun _getstatic(cls: Class<*>, name: String, description: Class<*>): InstructionBuilder {
        return _instruction(ByteCode.GETSTATIC) {
            _indexU2(constantPool.writeFieldRef(cls.path(), name, description.descriptor()))
        }
    }

    fun _getstatic(cls: String, name: String, description: String): InstructionBuilder {
        return _instruction(ByteCode.GETSTATIC) {
            _indexU2(constantPool.writeFieldRef(cls, name, description))
        }
    }

    fun _return(): InstructionBuilder {
        return _instruction(ByteCode.RETURN)
    }

    fun _invokeSpecial(cls: Class<*>, method: String, description: String): List<InstructionBuilder> {
        ensureStackCapacity(1u)

        return listOf(
            _instruction(ByteCode.ALOAD_0),
            _instruction(ByteCode.INVOKESPECIAL) {
                _indexU2(constantPool.writeMethodRef(cls.path(), method, description))
            }
        )
    }

    fun _invokeVirtual(cls: Class<*>, method: String, description: String): InstructionBuilder {
        ensureStackCapacity(2u)

        return _instruction(ByteCode.INVOKEVIRTUAL) {
            _indexU2(constantPool.writeMethodRef(cls.path(), method, description))
        }
    }

    val currentPos: UShort
        get() = (initialOffset + size).toUShort()

    val size: UShort
        get() = code.fold(0u) { acc, inst -> (acc + inst.size).toUShort() }

    fun _lazyInstruction(
        code: ByteCode,
        reserve: UShort,
        evaluate: LazyInstructionBuilder.() -> Unit
    ): LazyInstructionBuilder {
        val lazyInstructionBuilder = InstructionBuilder.lazy(code, reserve, evaluate)
        this.code += lazyInstructionBuilder

        return lazyInstructionBuilder
    }

    fun _lazyInstruction(code: ByteCode, evaluate: LazyInstructionBuilder.() -> Unit): LazyInstructionBuilder {
        val lazyInstructionBuilder = InstructionBuilder.lazy(code, code.expectedParameters, evaluate)
        this.code += lazyInstructionBuilder

        return lazyInstructionBuilder
    }

    fun _instruction(code: ByteCode, init: InstructionBuilder.() -> Unit): InstructionBuilder {
        val builder = InstructionBuilder.eager(code)
        builder.init()
        this.code.add(builder)
        return builder
    }

    fun _instruction(code: ByteCode): InstructionBuilder {
        val builder = InstructionBuilder.eager(code)
        this.code.add(builder)
        return builder
    }

    fun _stackFrame(init: StackFrameBuilder.() -> Unit) {
        val stackFrameBuilder = StackFrameBuilder(currentPos)
        stackFrameBuilder.init()
        frames.addAll(stackFrameBuilder.frames)
    }

    @JvmInline
    value class PrimitiveArrayType(val atype: UByte) {
        companion object {
            val T_BOOLEAN = PrimitiveArrayType(4u)
            val T_CHAR = PrimitiveArrayType(5u)
            val T_FLOAT = PrimitiveArrayType(6u)
            val T_DOUBLE = PrimitiveArrayType(7u)
            val T_BYTE = PrimitiveArrayType(8u)
            val T_SHORT = PrimitiveArrayType(9u)
            val T_INT = PrimitiveArrayType(10u)
            val T_LONG = PrimitiveArrayType(11u)
        }
    }

    fun _primitiveArray(type: PrimitiveArrayType): InstructionBuilder {
        return _instruction(ByteCode.NEWARRAY) {
            _atype(type.atype)
        }
    }

    fun _mathOperation(operation: ByteCode, a: ByteCode, b: ByteCode) {
        _instruction(a)
        _instruction(b)
        _instruction(operation)
    }

    fun _goto(absolute: Short): InstructionBuilder {
        return _instruction(ByteCode.GOTO) {
            _position(absolute)
        }
    }

    fun _goto(label: Label): InstructionBuilder {
        return _goto((label.position.toInt() - currentPos.toInt()).toShort())
    }

    fun _goto(label: String): LazyInstructionBuilder {
        val position = currentPos
        return _lazyInstruction(ByteCode.GOTO) {
            val evaluatedLabel = labels[label] ?: throw IllegalStateException("Unable to find label `${label}`")
            _position((evaluatedLabel.position.toInt() - position.toInt()).toShort())
        }
    }

    fun label(name: String): Label {
        val label = Label(name, currentPos)
        labels[name] = label

        return label
    }

    fun label(): Label {
        return label("Position: $currentPos")
    }

    fun _loadIntOnStack(num: Int): InstructionBuilder {
        return when (num) {
            -1 -> _instruction(ByteCode.ICONST_M1)
            0 -> _instruction(ByteCode.ICONST_0)
            1 -> _instruction(ByteCode.ICONST_1)
            2 -> _instruction(ByteCode.ICONST_2)
            3 -> _instruction(ByteCode.ICONST_3)
            4 -> _instruction(ByteCode.ICONST_4)
            5 -> _instruction(ByteCode.ICONST_5)
            in 5..255 -> _instruction(ByteCode.BIPUSH) { _const(num.toByte()) }
            in 256..65535 -> _instruction(ByteCode.SIPUSH) { _value(num.toShort()) }
            else -> _ldc(num)
        }
    }

    fun _return(type: String): InstructionBuilder {
        return when (type) {
            in listOf("I", "Z", "C", "B") -> _instruction(ByteCode.IRETURN)
            "L" -> _instruction(ByteCode.LRETURN)
            "F" -> _instruction(ByteCode.FRETURN)
            "D" -> _instruction(ByteCode.DRETURN)
            else -> _instruction(ByteCode.ARETURN)
        }
    }

    data class ExceptionHandler(
        val startPc: UShort,
        val endPc: UShort,
        val handlerPc: UShort,
        val catchType: CPoolIndex
    )

    fun _exceptionHandler(startPc: UShort, endPc: UShort, handlerPc: UShort, catchType: CPoolIndex) {
        exceptionHandlers += ExceptionHandler(startPc, endPc, handlerPc, catchType)
    }

    fun _return(type: Class<*>): InstructionBuilder {
        return _return(type.descriptor())
    }

    operator fun plus(other: CodeBuilder): CodeBuilder {
        this.code += other.code
        this.frames += other.frames
        this.maxStack = max(maxStack, other.maxStack)
        this.maxLocals = max(maxLocals, other.maxLocals)
        return this
    }

    fun flush() : Code {
        var stackSize : UShort = 0u
        
        val instructions = mutableListOf<Instruction>()
        for (instructionBuilder in instructions()) {
            instructionBuilder.flush()

            val code : ByteCode = instructionBuilder.code
            if (code.resetStack != null) {
                stackSize = code.resetStack.toUShort()
            } else {
                stackSize = if(code.stackChange > stackSize.toInt()) {
                    0u
                } else {
                    (stackSize.toInt() + code.stackChange).toUShort()
                }
            }

            this.maxStack = max(maxStack, stackSize)
            instructions += Instruction(code, instructionBuilder.operands)
        }
        
        return Code(maxStack, maxLocals, instructions)
    }

    fun _exception(function: ExceptionBuilder.() -> Unit) : ExceptionBuilder {
        val exceptionBuilder = ExceptionBuilder(currentPos, constantPool, labels)
        exceptionBuilder.function()
        code += exceptionBuilder
        return exceptionBuilder
    }

    fun build(): Code {
        var stackSize : UShort = 0u

        val instructions = mutableListOf<Instruction>()
        for (instructionBuilder in instructions()) {
            instructionBuilder.flush()

            val code : ByteCode = instructionBuilder.code
            if (code.resetStack != null) {
                stackSize = code.resetStack.toUShort()
            } else {
                stackSize = if(code.stackChange > stackSize.toInt()) {
                    0u
                } else {
                    (stackSize.toInt() + code.stackChange).toUShort()
                }
            }

            this.maxStack = max(maxStack, stackSize)
            instructions += Instruction(code, instructionBuilder.operands)
        }

        return Code(maxStack, maxLocals, instructions)
    }
}