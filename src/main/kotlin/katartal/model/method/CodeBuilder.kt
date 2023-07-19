package katartal.model.method

import katartal.model.ByteCode
import katartal.model.CPoolIndex
import katartal.model.ConstantPool
import katartal.model.method.instruction.InstructionBuilder
import katartal.model.method.instruction.LazyInstructionBuilder
import katartal.util.descriptor
import katartal.util.path
import kotlin.math.max

class CodeBuilder(
    var maxLocals: Int = -1,
    var maxStack: Int = -1,
    private val initialOffset: UShort = 0u,
    internal val labels: MutableMap<String, Label>,
    internal val variables: MutableList<LocalVariable>,
    internal val constantPool: ConstantPool
) {
    val instructions = mutableListOf<InstructionBuilder>()
    val frames = mutableListOf<StackFrameBuilder.StackFrame>()

    private fun ensureStackCapacity(minStackSize: Int) {
        maxStack = max(maxStack, minStackSize)
    }

    fun _return(boolean: Boolean) {
        ensureStackCapacity(1)

        _instruction(if (boolean) ByteCode.ICONST_1 else ByteCode.ICONST_0)
        _instruction(ByteCode.IRETURN)
    }

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
        ensureStackCapacity(1)

        return listOf(
            _instruction(ByteCode.ALOAD_0),
            _instruction(ByteCode.INVOKESPECIAL) {
                _indexU2(constantPool.writeMethodRef(cls.path(), method, description))
            }
        )
    }

    fun _invokeVirtual(cls: Class<*>, method: String, description: String): InstructionBuilder {
        ensureStackCapacity(2)

        return _instruction(ByteCode.INVOKEVIRTUAL) {
            _indexU2(constantPool.writeMethodRef(cls.path(), method, description))
        }
    }

    val currentPos: UShort
        get() = (initialOffset + size).toUShort()

    val size: UShort
        get() = instructions.fold(0) { acc, inst -> acc + inst.size }.toUShort()

    fun _lazyInstruction(
        code: ByteCode,
        reserve: Int,
        evaluate: LazyInstructionBuilder.() -> Unit
    ): LazyInstructionBuilder {
        val lazyInstructionBuilder = InstructionBuilder.lazy(code, reserve, evaluate)
        instructions += lazyInstructionBuilder

        return lazyInstructionBuilder
    }

    fun _lazyInstruction(code: ByteCode, evaluate: LazyInstructionBuilder.() -> Unit): LazyInstructionBuilder {
        val lazyInstructionBuilder = InstructionBuilder.lazy(code, code.expectedParameters, evaluate)
        instructions += lazyInstructionBuilder

        return lazyInstructionBuilder
    }

    fun _instruction(code: ByteCode, init: InstructionBuilder.() -> Unit): InstructionBuilder {
        val builder = InstructionBuilder.eager(code)
        builder.init()
        instructions.add(builder)
        return builder
    }

    fun _instruction(code: ByteCode): InstructionBuilder {
        val builder = InstructionBuilder.eager(code)
        instructions.add(builder)
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

    fun _goto(label: Label): InstructionBuilder {
        return _instruction(ByteCode.GOTO) {
            _position((label.position.toInt() - currentPos.toInt()).toShort())
        }
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

    fun _goto(absolute: Short): InstructionBuilder {
        return _instruction(ByteCode.GOTO) {
            _position(absolute)
        }
    }

    fun _iconst(num: Int): InstructionBuilder {
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
            else -> _instruction(ByteCode.LDC) { _indexU2(constantPool.writeInteger(num)) }
        }
    }


    operator fun plus(other: CodeBuilder): CodeBuilder {
        this.instructions += other.instructions
        this.frames += other.frames
        this.maxStack = max(maxStack, other.maxStack)
        this.maxLocals = max(maxLocals, other.maxLocals)
        return this
    }

    fun flush() {
        var stackSize = 0
        for (instruction in instructions) {
            instruction.flush()
            stackSize += instruction.code.stackChange
            this.maxStack = max(maxStack, stackSize)
        }
        
        println("Max stack ${maxStack}")
    }
}