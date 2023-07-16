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
class StackMapTableAttribute(attributeNameIndex: UShort, private val frames: List<StackMapFrame>) :
    Attribute(attributeNameIndex) {
    override fun generateAttributeData(): ByteArray {
        val localVarAttributeArray = DynamicByteArray()
        localVarAttributeArray.putU2(frames.size)

        for (frame in frames) {
        }

        return localVarAttributeArray.toByteArray()
    }
}

enum class StackMapFrameType {
    SAME,
    SAME_LOCALS_1_STACK_ITEM,
    SAME_LOCALS_1_STACK_ITEM_EXTENDED,
    CHOP,
    SAME_FRAME_EXTENDED,
    APPEND,
    FULL_FRAME
}

sealed class StackMapFrame(val frameType: StackMapFrameType)
class same_frame : StackMapFrame(StackMapFrameType.SAME)
class same_locals_1_stack_item_frame(val verificationTypeInfo: VerificationTypeInfo) :
    StackMapFrame(StackMapFrameType.SAME_LOCALS_1_STACK_ITEM)

class same_locals_1_stack_item_frame_extended(val offsetDelta: UShort, val verificationTypeInfo: VerificationTypeInfo) :
    StackMapFrame(StackMapFrameType.SAME_LOCALS_1_STACK_ITEM_EXTENDED)

class chop_frame(val offsetDelta: UShort) : StackMapFrame(StackMapFrameType.CHOP)
class same_frame_extended(val offsetDelta: UShort) : StackMapFrame(StackMapFrameType.SAME_FRAME_EXTENDED)
class append_frame(val offsetDelta: UShort, val locals: List<VerificationTypeInfo>) :
    StackMapFrame(StackMapFrameType.APPEND)

class full_frame(
    val offsetDelta: UShort,
    val numberOfLocals: UShort,
    val locals: List<VerificationTypeInfo>,
    numberOfStackItems: UShort,
    val stack: List<VerificationTypeInfo>
) : StackMapFrame(StackMapFrameType.FULL_FRAME)

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

sealed class VerificationTypeInfo(val tag: VerificationTypeTag)
class Top_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Top)
class Integer_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Integer)
class Float_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Float)
class Double_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Double)
class Long_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Long)
class Null_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Null)
class UninitializedThis_variable_info(val offset: UShort) :
    VerificationTypeInfo(VerificationTypeTag.ITEM_UninitializedThis)

class Object_variable_info(val cPoolIndex: CPoolIndex) : VerificationTypeInfo(VerificationTypeTag.ITEM_Object)