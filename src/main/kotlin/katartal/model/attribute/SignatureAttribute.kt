package katartal.model.attribute

import katartal.model.CPoolIndex

class SignatureAttribute(attributeNameIndex: CPoolIndex, private val signatureIndex: CPoolIndex) :
    Attribute(attributeNameIndex),
    ClassAttribute,
    MethodAttribute,
    FieldAttribute,
    RecordComponentAttribute {

    override fun generateAttributeData(): ByteArray {
        return ByteArray(2).apply {
            val (high, low) = signatureIndex
            set(0, high.toByte())
            set(1, low.toByte())
        }
    }
}