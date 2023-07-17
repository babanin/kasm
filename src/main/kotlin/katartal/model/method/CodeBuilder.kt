package katartal.model.method

import katartal.model.ByteCode
import katartal.model.ConstantPool
import katartal.model.StackMapFrameAttribute
import katartal.util.descriptor
import katartal.util.path
import kotlin.math.max

class CodeBuilder(
    var maxLocals: Int = -1,
    var maxStack: Int = -1,
    val initialOffset: UShort = 0u,
    private val constantPool: ConstantPool
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

    fun _ldc(cpIndex: UShort): InstructionBuilder {
        if (cpIndex > 255u) {
            return _instruction(ByteCode.LDC_W) {
                _referenceU2(cpIndex)
            }
        }

        return _instruction(ByteCode.LDC) {
            _referenceU1(cpIndex)
        }
    }

    fun _ldc(value: String): InstructionBuilder {
        return _ldc(constantPool.writeString(value))
    }

    fun _getstatic(cls: Class<*>, name: String, description: Class<*>): InstructionBuilder {
        return _instruction(ByteCode.GETSTATIC) {
            _referenceU2(constantPool.writeFieldRef(cls.path(), name, description.descriptor()))
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
                _referenceU2(constantPool.writeMethodRef(cls.path(), method, description))
            }
        )
    }

    fun _invokeVirtual(cls: Class<*>, method: String, description: String): InstructionBuilder {
        ensureStackCapacity(2)

        return _instruction(ByteCode.INVOKEVIRTUAL) {
            _referenceU2(constantPool.writeMethodRef(cls.path(), method, description))
        }
    }

    fun _if(code: ByteCode, subRoutine: CodeBuilder.() -> Unit): List<InstructionBuilder> {
        val ifItself : UByte = (1u + 2u).toUByte()
        
        val codeBuilder = CodeBuilder(initialOffset = (currentPos + ifItself).toUShort(), constantPool = constantPool)
        codeBuilder.subRoutine()

        val codeLength = codeBuilder.size

        val ifInst = _instruction(code) {
            _referenceU2((ifItself + codeLength).toUShort())
        }

        instructions += codeBuilder.instructions
        this.maxStack = max(maxStack, codeBuilder.maxStack)

        val inst = mutableListOf<InstructionBuilder>()
        inst += ifInst
        inst += codeBuilder.instructions
        return inst
    }

    val currentPos: UShort
        get() = (initialOffset + size).toUShort()
    
    val size: UShort
        get() = instructions.fold(0) { acc, inst -> acc + inst.size }.toUShort()

    fun label(): Label {
        return Label(currentPos)
    }
    
    data class Label(val position: UShort) 

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

    fun _nop(): InstructionBuilder {
        return _instruction(ByteCode.NOP)
    }

    fun _goto(label: Label): InstructionBuilder {
        return _instruction(ByteCode.GOTO) {
            _position((label.position.toInt() - currentPos.toInt()).toShort())
        }
    }

    operator fun plus(other: CodeBuilder): CodeBuilder {
        this.instructions += other.instructions
        this.frames += other.frames
        this.maxStack = max(maxStack, other.maxStack)
        this.maxLocals = max(maxLocals, other.maxLocals)
        return this
    }
}