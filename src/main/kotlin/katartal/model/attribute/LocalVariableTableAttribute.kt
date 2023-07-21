package katartal.model.attribute

import katartal.model.CPoolIndex
import katartal.util.DynamicByteArray

data class LocalVariableTableEntry(
    val startPc: UShort,
    val length: UShort,
    val nameIndex: CPoolIndex,
    val descriptorIndex: CPoolIndex,
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
class LocalVariableTableAttribute(attributeNameIndex: CPoolIndex, private val entries: List<LocalVariableTableEntry>) :
    Attribute(attributeNameIndex), MethodAttribute {
    override fun generateAttributeData(): ByteArray {
        val localVarAttributeArray = DynamicByteArray()
        localVarAttributeArray.putU2(entries.size)

        for (entry in entries) {
            localVarAttributeArray.putU2(entry.startPc)
            localVarAttributeArray.putU2(entry.length)
            localVarAttributeArray.putU2(entry.nameIndex.toUInt())
            localVarAttributeArray.putU2(entry.descriptorIndex.toUInt())
            localVarAttributeArray.putU2(entry.index)
        }

        return localVarAttributeArray.toByteArray()
    }
}
