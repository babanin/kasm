@file:Suppress("EnumEntryName")

package katartal.model

import katartal.util.DynamicByteArray

sealed class Attribute(val attributeNameIndex: UShort) {
    fun toByteArray(): ByteArray {
        val attributeData = generateAttributeData()

        val result = DynamicByteArray()
        result.putU2(attributeNameIndex)
        result.putU4(attributeData.size)
        result.putByteArray(attributeData)
        return result.toByteArray()
    }

    abstract fun generateAttributeData(): ByteArray
}

data class ExceptionEntry(val startPc: Int, val endPc: Int, val handlerPc: Int, val catchType: Int)

/**
 * Code_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 max_stack;
 *     u2 max_locals;
 *     u4 code_length;
 *     u1 code[code_length];
 *     u2 exception_table_length;
 *     {   u2 start_pc;
 *         u2 end_pc;
 *         u2 handler_pc;
 *         u2 catch_type;
 *     } exception_table[exception_table_length];
 *     u2 attributes_count;
 *     attribute_info attributes[attributes_count];
 * }
 */
class CodeAttribute(
    attributeNameIndex: UShort,
    private val maxStack: UShort,
    private val maxLocals: UShort,
    private val code: ByteArray,
    private val exceptionTable: List<ExceptionEntry> = listOf(),
    private val attributes: List<Attribute> = listOf()
) :
    Attribute(attributeNameIndex) {

    override fun generateAttributeData(): ByteArray {
        val codeAttributeArray = DynamicByteArray()
        codeAttributeArray.putU2(maxStack)
        codeAttributeArray.putU2(maxLocals)
        codeAttributeArray.putU4(code.size)
        codeAttributeArray.putByteArray(code)
        codeAttributeArray.putU2(exceptionTable.size)

        for (exception in exceptionTable) {
            codeAttributeArray.putU2(exception.startPc)
            codeAttributeArray.putU2(exception.endPc)
            codeAttributeArray.putU2(exception.handlerPc)
            codeAttributeArray.putU2(exception.catchType)
        }

        codeAttributeArray.putU2(attributes.size)
        for (subAttributeSerialized in attributes) {
            codeAttributeArray.putByteArray(subAttributeSerialized.toByteArray())
        }

        return codeAttributeArray.toByteArray()
    }
}

data class LocalVariableTableEntry(
    val startPc: UShort,
    val length: UShort,
    val nameIndex: UShort,
    val descriptorIndex: UShort,
    val index: UShort
)

/**
 * LocalVariableTable_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 local_variable_table_length;
 *     {   u2 start_pc;
 *         u2 length;
 *         u2 name_index;
 *         u2 descriptor_index;
 *         u2 index;
 *     } local_variable_table[local_variable_table_length];
 * }
 */
class LocalVariableTable(attributeNameIndex: UShort, private val entries: List<LocalVariableTableEntry>) :
    Attribute(attributeNameIndex) {
    override fun generateAttributeData(): ByteArray {
        val localVarAttributeArray = DynamicByteArray()
        localVarAttributeArray.putU2(entries.size)

        for (entry in entries) {
            localVarAttributeArray.putU2(entry.startPc)
            localVarAttributeArray.putU2(entry.length)
            localVarAttributeArray.putU2(entry.nameIndex)
            localVarAttributeArray.putU2(entry.descriptorIndex)
            localVarAttributeArray.putU2(entry.index)
        }

        return localVarAttributeArray.toByteArray()
    }
}

/**
 * StackMapTable_attribute {
 *     u2              attribute_name_index;
 *     u4              attribute_length;
 *     u2              number_of_entries;
 *     stack_map_frame entries[number_of_entries];
 * }
 */
class StackMapTableAttribute(attributeNameIndex: UShort, private val frames: List<StackMapFrameAttribute>) :
    Attribute(attributeNameIndex) {
    override fun generateAttributeData(): ByteArray {
        val localVarAttributeArray = DynamicByteArray()
        localVarAttributeArray.putU2(frames.size)

        for (frame in frames) {
            localVarAttributeArray.putByteArray(frame.toByteArray())
        }

        return localVarAttributeArray.toByteArray()
    }
}

sealed class StackMapFrameAttribute(val frameType: UByte) {
    fun toByteArray(): ByteArray {
        val dynamicByteArray = DynamicByteArray()
        dynamicByteArray.putU1(frameType)

        writeCustomData(dynamicByteArray)

        return dynamicByteArray.toByteArray()
    }

    open fun writeCustomData(dynamicByteArray: DynamicByteArray) = Unit
}

class same_frame(frameType: UByte) : StackMapFrameAttribute(frameType) {
    init {
        if (frameType > 63u) {
            throw IllegalStateException("same_frame is represented by tags in the range [0-63]")
        }
    }
}

class same_locals_1_stack_item_frame(frameType: UByte, private val verificationTypeInfo: VerificationTypeInfo) :
    StackMapFrameAttribute(frameType) {
    init {
        if (frameType < 64u || frameType > 127u) {
            throw IllegalStateException("same_locals_1_stack_item_frame is represented by tags in the range [64, 127]")
        }
    }

    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU1(verificationTypeInfo.tag.code)
    }
}

class same_locals_1_stack_item_frame_extended(
    private val offsetDelta: UShort,
    private val verificationTypeInfo: VerificationTypeInfo
) :
    StackMapFrameAttribute(247u) {
    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU2(offsetDelta)
        dynamicByteArray.putU1(verificationTypeInfo.tag.code)
    }
}

class chop_frame(private val offsetDelta: UShort, k: UByte) : StackMapFrameAttribute((251u - k).toUByte()) {
    init {
        if (frameType < 248u || frameType > 250u) {
            throw IllegalStateException("chop_frame is represented by tags in the range [248-250]")
        }
    }

    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU2(offsetDelta)
    }

    override fun toString(): String {
        return "chop_frame offset=${offsetDelta}"
    }
}

class same_frame_extended(private val offsetDelta: UShort) : StackMapFrameAttribute(251u) {
    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU2(offsetDelta)
    }
}

class append_frame(
    private val offsetDelta: UShort,
    private val locals: List<VerificationTypeInfo>
) :
    StackMapFrameAttribute((251 + locals.size).toUByte()) {

    init {
        if (frameType < 252u || frameType > 254u) {
            throw IllegalStateException("append_frame is represented by tags in the range [252-254]")
        }
    }

    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU2(offsetDelta)

        for (local in locals) {
            dynamicByteArray.putByteArray(local.toByteArray())
        }
    }

    override fun toString(): String {
        return "append_frame offset=${offsetDelta} locals=${locals}"
    }
}

class full_frame(
    private val offsetDelta: UShort,
    private val locals: List<VerificationTypeInfo>,
    private val stacks: List<VerificationTypeInfo>
) : StackMapFrameAttribute(255u) {
    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU2(offsetDelta)

        dynamicByteArray.putU2(locals.size)
        for (local in locals) {
            dynamicByteArray.putByteArray(local.toByteArray())
        }

        dynamicByteArray.putU2(stacks.size)
        for (stack in stacks) {
            dynamicByteArray.putByteArray(stack.toByteArray())
        }
    }
}

enum class VerificationTypeTag(val code: UByte) {
    ITEM_Top(0u),
    ITEM_Integer(1u),
    ITEM_Float(2u),
    ITEM_Double(3u),
    ITEM_Long(4u),
    ITEM_Null(5u),
    ITEM_UninitializedThis(6u),
    ITEM_Object(7u),
}

sealed class VerificationTypeInfo(val tag: VerificationTypeTag) {
    open fun toByteArray(): ByteArray {
        return byteArrayOf(tag.code.toByte())
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}(tag=$tag)"
    }
}

class Top_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Top)
class Integer_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Integer)
class Float_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Float)
class Double_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Double)
class Long_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Long)
class Null_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Null)
class UninitializedThis_variable_info(private val offset: UShort) :
    VerificationTypeInfo(VerificationTypeTag.ITEM_UninitializedThis) {
    override fun toByteArray(): ByteArray {
        return DynamicByteArray(3).apply {
            putU1(tag.code)
            putU2(offset)
        }.toByteArray()
    }

    override fun toString(): String {
        return "UninitializedThis_variable_info(offset=$offset)"
    }
}

class Object_variable_info(private val cPoolIndex: CPoolIndex) : VerificationTypeInfo(VerificationTypeTag.ITEM_Object) {
    override fun toByteArray(): ByteArray {
        return DynamicByteArray(3).apply {
            putU1(tag.code)
            putU2(cPoolIndex.index)
        }.toByteArray()
    }

    override fun toString(): String {
        return "Object_variable_info(cPoolIndex=$cPoolIndex)"
    }


}