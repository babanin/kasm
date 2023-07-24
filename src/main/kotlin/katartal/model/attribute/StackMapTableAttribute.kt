@file:Suppress("ClassName")

package katartal.model.attribute

import katartal.model.CPoolIndex
import katartal.util.DynamicByteArray

/**
 * StackMapTable_attribute {
 *     u2              attribute_name_index;
 *     u4              attribute_length;
 *     u2              number_of_entries;
 *     stack_map_frame entries[number_of_entries];
 * }
 */
class StackMapTableAttribute(attributeNameIndex: CPoolIndex, private val frames: List<StackMapFrameAttribute>) :
    Attribute(attributeNameIndex), MethodCodeAttribute {
    override fun generateAttributeData(): ByteArray {
        val localVarAttributeArray = DynamicByteArray()
        localVarAttributeArray.putU2(frames.size)

        for (frame in frames) {
            localVarAttributeArray.putByteArray(frame.toByteArray())
        }

        return localVarAttributeArray.toByteArray()
    }
}

/**
 * union stack_map_frame {
 *     same_frame;
 *     same_locals_1_stack_item_frame;
 *     same_locals_1_stack_item_frame_extended;
 *     chop_frame;
 *     same_frame_extended;
 *     append_frame;
 *     full_frame;
 * }
 */
sealed class StackMapFrameAttribute(val frameType: UByte) {
    fun toByteArray(): ByteArray {
        val dynamicByteArray = DynamicByteArray()
        dynamicByteArray.putU1(frameType)

        writeCustomData(dynamicByteArray)

        return dynamicByteArray.toByteArray()
    }

    open fun writeCustomData(dynamicByteArray: DynamicByteArray) = Unit

    override fun toString(): String = "${this.javaClass.simpleName} frameType=${frameType}"
}

/**
 * The frame type same_frame is represented by tags in the range [0-63].
 * This frame type indicates that the frame has exactly the same local variables as the previous frame and that
 * the operand stack is empty. The offset_delta value for the frame is the value of the tag item, frame_type.
 */
class same_frame(frameType: UByte) : StackMapFrameAttribute(frameType) {
    init {
        if (frameType > 63u) {
            throw IllegalStateException(
                "Invalid frame type: ${frameType}. " +
                        "same_frame is represented by tags in the range [0-63]."
            )
        }
    }
}

/**
 * The frame type same_locals_1_stack_item_frame is represented by tags in the range [64, 127].
 * If the frame_type is same_locals_1_stack_item_frame, it means the frame has exactly the same locals as the
 * previous stack map frame and that the number of stack items is 1.
 * The offset_delta value for the frame is the value (frame_type - 64).
 * There is a verification_type_info following the frame_type for the one stack item.
 */
class same_locals_1_stack_item_frame(offset: UShort, private val verificationTypeInfo: VerificationTypeInfo) :
    StackMapFrameAttribute((offset + 64u).toUByte()) {

    init {
        if (frameType < 64u || frameType > 127u) {
            throw IllegalStateException(
                "Invalid frame type: ${frameType}. " +
                        "same_locals_1_stack_item_frame is represented by tags in the range [64, 127]"
            )
        }
    }

    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU1(verificationTypeInfo.tag.code)
        dynamicByteArray.putByteArray(verificationTypeInfo.toByteArray())
    }
}

class same_locals_1_stack_item_frame_extended(
    private val offsetDelta: UShort,
    private val verificationTypeInfo: VerificationTypeInfo
) :
    StackMapFrameAttribute(247u) {
    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU1(verificationTypeInfo.tag.code)
        dynamicByteArray.putU2(offsetDelta)
        dynamicByteArray.putByteArray(verificationTypeInfo.toByteArray())
    }
}

/**
 * The frame type chop_frame is represented by tags in the range [248-250].
 * If the frame_type is chop_frame, it means that the operand stack is empty and the current locals are the
 * same as the locals in the previous frame, except that the k last locals are absent.
 * The value of k is given by the formula 251 - frame_type.
 */
class chop_frame(private val offsetDelta: UShort, k: UByte) : StackMapFrameAttribute((251u - k).toUByte()) {
    init {
        if (frameType < 248u || frameType > 250u) {
            throw IllegalStateException(
                "Invalid frame type: ${frameType}. " +
                        "chop_frame is represented by tags in the range [248-250]"
            )
        }
    }

    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU2(offsetDelta)
    }

    override fun toString(): String = "chop_frame offset=${offsetDelta}"
}

class same_frame_extended(private val offsetDelta: UShort) : StackMapFrameAttribute(251u) {
    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU2(offsetDelta)
    }
}

/**
 * The frame type append_frame is represented by tags in the range [252-254].
 * If the frame_type is append_frame, it means that the operand stack is empty and the current locals are the
 * same as the locals in the previous frame, except that k additional locals are defined.
 * The value of k is given by the formula frame_type - 251.
 */
class append_frame(
    private val offsetDelta: UShort,
    private val locals: List<VerificationTypeInfo>
) :
    StackMapFrameAttribute((251 + locals.size).toUByte()) {

    init {
        if (frameType < 252u || frameType > 254u) {
            throw IllegalStateException(
                "Invalid frame type: ${frameType}. " +
                        "append_frame is represented by tags in the range [252-254]"
            )
        }
    }

    override fun writeCustomData(dynamicByteArray: DynamicByteArray) {
        dynamicByteArray.putU2(offsetDelta)

        for (local in locals) {
            dynamicByteArray.putByteArray(local.toByteArray())
        }
    }

    override fun toString(): String = "append_frame offset=${offsetDelta} locals=${locals}"
}

/**
 * The 0th entry in locals represents the verification type of local variable 0. If locals[M] represents local variable N, then:
 *  locals[M+1] represents local variable N+1 if locals[M] is one of
 *      Top_variable_info,
 *      Integer_variable_info,
 *      Float_variable_info,
 *      Null_variable_info,
 *      UninitializedThis_variable_info,
 *      Object_variable_info, or
 *      Uninitialized_variable_info; and
 *
 *  locals[M+1] represents local variable N+2 if locals[M] is either
 *      Long_variable_info,
 *      Double_variable_info.
 *
 * It is an error if, for any index i, locals[i] represents a local variable whose index is greater than the
 * maximum number of local variables for the method.
 *
 * The 0th entry in stack represents the verification type of the bottom of the operand stack, and
 * subsequent entries in stack represent the verification types of stack entries closer to the top of the operand
 * stack. We refer to the bottom of the operand stack as stack entry 0, and to subsequent entries of the operand
 * stack as stack entry 1, 2, etc. If stack[M] represents stack entry N, then:
 *
 *  stack[M+1] represents stack entry N+1 if stack[M] is one of
 *      Top_variable_info,
 *      Integer_variable_info,
 *      Float_variable_info,
 *      Null_variable_info,
 *      UninitializedThis_variable_info,
 *      Object_variable_info, or
 *      Uninitialized_variable_info; and
 *
 *  stack[M+1] represents stack entry N+2 if stack[M] is either
 *      Long_variable_info or
 *      Double_variable_info.
 *
 * It is an error if, for any index i, stack[i] represents a stack entry whose index is greater than the maximum
 * operand stack size for the method.
 */
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
    ITEM_Uninitialized(8u)
}

/**
 * union verification_type_info {
 *     Top_variable_info;
 *     Integer_variable_info;
 *     Float_variable_info;
 *     Long_variable_info;
 *     Double_variable_info;
 *     Null_variable_info;
 *     UninitializedThis_variable_info;
 *     Object_variable_info;
 *     Uninitialized_variable_info;
 * }
 */
sealed class VerificationTypeInfo(val tag: VerificationTypeTag) {
    open fun toByteArray(): ByteArray {
        return byteArrayOf(tag.code.toByte())
    }

    override fun toString(): String = "${this.javaClass.simpleName}(tag=$tag)"
}

/**
 * Top_variable_info {
 *     u1 tag = ITEM_Top; /* 0 */
 * }
 */
class Top_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Top)

/**
 * Integer_variable_info {
 *     u1 tag = ITEM_Integer; /* 1 */
 * }
 */
class Integer_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Integer)

/**
 * Float_variable_info {
 *     u1 tag = ITEM_Float; /* 2 */
 * }
 */
class Float_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Float)

/**
 * Double_variable_info {
 *     u1 tag = ITEM_Double; /* 3 */
 * }
 */
class Double_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Double)

/**
 * Long_variable_info {
 *     u1 tag = ITEM_Long; /* 4 */
 * }
 */
class Long_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Long)

/**
 * Null_variable_info {
 *     u1 tag = ITEM_Null; /* 5 */
 * }
 */
class Null_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_Null)

/**
 * UninitializedThis_variable_info {
 *     u1 tag = ITEM_UninitializedThis; /* 6 */
 * }
 */
class UninitializedThis_variable_info : VerificationTypeInfo(VerificationTypeTag.ITEM_UninitializedThis)

/**
 * Uninitialized_variable_info {
 *     u1 tag = ITEM_Uninitialized; /* 8 */
 *     u2 offset;
 * }
 */
class Uninitialized_variable_info(private val offset: UShort) :
    VerificationTypeInfo(VerificationTypeTag.ITEM_Uninitialized) {
    override fun toByteArray(): ByteArray {
        return DynamicByteArray(3).apply {
            putU1(tag.code)
            putU2(offset)
        }.toByteArray()
    }

    override fun toString(): String = "${this.javaClass.simpleName}(tag=$tag, offset=$offset)"
}

/**
 * Object_variable_info {
 *     u1 tag = ITEM_Object; /* 7 */
 *     u2 cpool_index;
 * }
 */
class Object_variable_info(private val cPoolIndex: CPoolIndex) : VerificationTypeInfo(VerificationTypeTag.ITEM_Object) {
    override fun toByteArray(): ByteArray {
        return DynamicByteArray(3).apply {
            putU1(tag.code)
            putU2(cPoolIndex.index)
        }.toByteArray()
    }

    override fun toString(): String = "${this.javaClass.simpleName}(tag=$tag, cPoolIndex=$cPoolIndex)"
}