package katartal.model.attribute

import katartal.model.CPoolIndex
import katartal.util.DynamicByteArray

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
    attributeNameIndex: CPoolIndex,
    private val maxStack: UShort,
    private val maxLocals: UShort,
    private val code: ByteArray,
    private val exceptionTable: List<ExceptionEntry> = listOf(),
    private val attributes: List<MethodCodeAttribute> = listOf()
) :
    Attribute(attributeNameIndex), MethodAttribute {

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

data class ExceptionEntry(val startPc: Int, val endPc: Int, val handlerPc: Int, val catchType: Int)
