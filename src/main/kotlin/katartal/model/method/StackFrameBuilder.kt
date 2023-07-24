package katartal.model.method

import katartal.util.descriptor
import katartal.util.path

class StackFrameBuilder(private val absoluteOffset: UShort) {
    val frames = mutableListOf<StackFrame>()

    /**
     * The frame type same_frame is represented by tags in the range [0-63].
     * This frame type indicates that the frame has exactly the same local variables as the previous frame and that
     * the operand stack is empty. The offset_delta value for the frame is the value of the tag item, frame_type.
     */
    fun _same(): SameFrame {
        val sameFrame = SameFrame(absoluteOffset)
        frames += sameFrame
        return sameFrame
    }
    
    /**
     * The frame type same_locals_1_stack_item_frame is represented by tags in the range [64, 127].
     * If the frame_type is same_locals_1_stack_item_frame, it means the frame has exactly the same locals as the
     * previous stack map frame and that the number of stack items is 1.
     * The offset_delta value for the frame is the value (frame_type - 64).
     * There is a verification_type_info following the frame_type for the one stack item.
     */
    fun _same_locals_1_stack_item(local: Type): SameLocals1StackItem {
        val sameFrame = SameLocals1StackItem(absoluteOffset, local)
        frames += sameFrame
        return sameFrame
    }


    /**
     * The frame type append_frame is represented by tags in the range [252-254].
     * If the frame_type is append_frame, it means that the operand stack is empty and the current locals are the
     * same as the locals in the previous frame, except that k additional locals are defined.
     * The value of k is given by the formula frame_type - 251.
     */
    fun _append(vararg locals: Type): AppendFrame {
        val appendFrame = AppendFrame(absoluteOffset, locals.toList())
        frames += appendFrame
        return appendFrame
    }

    /**
     * The frame type chop_frame is represented by tags in the range [248-250]. 
     * If the frame_type is chop_frame, it means that the operand stack is empty and the current locals are the 
     * same as the locals in the previous frame, except that the k last locals are absent. 
     * The value of k is given by the formula 251 - frame_type.
     */
    fun _chop(k: Int): ChopFrame {
        val chopFrame = ChopFrame(absoluteOffset, k.toUByte())
        frames += chopFrame
        return chopFrame
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
    fun _full(locals: List<Type> = listOf(), stacks: List<Type> = listOf()): FullFrame {
        val fullFrame = FullFrame(absoluteOffset, locals, stacks)
        frames += fullFrame
        return fullFrame
    }

    sealed class StackFrame(val absoluteOffset: UShort)
    class SameFrame(absoluteOffset: UShort) : StackFrame(absoluteOffset)
    class SameLocals1StackItem(absoluteOffset: UShort, val local: Type) : StackFrame(absoluteOffset)
    class AppendFrame(absoluteOffset: UShort, val locals: List<Type>) : StackFrame(absoluteOffset)
    class ChopFrame(absoluteOffset: UShort, val k: UByte) : StackFrame(absoluteOffset)
    class FullFrame(absoluteOffset: UShort, val locals: List<Type>, val stacks: List<Type>) : StackFrame(absoluteOffset)

    sealed class Type
    class TopVar : Type()
    class IntegerVar : Type()
    class FloatVar : Type()
    class DoubleVar : Type()
    class LongVar : Type()
    class NullVar : Type()
    class UninitializedThisVar : Type()
    class UninitializedVar(val offset: UShort) : Type()
    class ObjectVar(val cls: String) : Type() {
        constructor(cls: Class<*>) : this(cls.path())
    }


}