package katartal.model.attribute

import katartal.model.CPoolIndex
import katartal.util.DynamicByteArray

/**
 * The Exceptions attribute is a variable-length attribute in the attributes table of a method_info structure (§4.6).
 * The Exceptions attribute indicates which checked exceptions a method may throw.
 * There may be at most one Exceptions attribute in each method_info structure.
 */
class MethodExceptionsAttribute(
    attributeNameIndex: CPoolIndex,
    private val exceptionClassIdx: List<CPoolIndex>
) :
    Attribute(attributeNameIndex), MethodAttribute {
    override fun generateAttributeData(): ByteArray {
        return DynamicByteArray(2 + exceptionClassIdx.size * 2).apply {
            putU2(exceptionClassIdx.size)
            for (idx in exceptionClassIdx) {
                putU2(idx.toUInt())
            }
        }.toByteArray()
    }
}