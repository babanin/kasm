package katartal.model.attribute

import katartal.model.CPoolIndex

class ConstantValueAttribute(attributeNameIndex: CPoolIndex, private val constantValueIndex: CPoolIndex) :
    Attribute(attributeNameIndex), FieldAttribute {
    override fun generateAttributeData(): ByteArray {
        return ByteArray(2).apply {
            val (high, low) = constantValueIndex
            set(0, high.toByte())
            set(1, low.toByte())
        }
    }
}