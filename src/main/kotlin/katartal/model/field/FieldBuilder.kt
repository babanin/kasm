package katartal.model.field

import katartal.model.CPoolIndex
import katartal.model.ConstantPool
import katartal.model.attribute.FieldAttribute
import katartal.model.attribute.SignatureAttribute

open class FieldBuilder(
    val name: String,
    val descriptor: String,
    val access: FieldAccess,
    private val constantPool: ConstantPool
) {
    val nameCpIndex: CPoolIndex = constantPool.writeUtf8(name)
    val descriptorCpIndex: CPoolIndex = constantPool.writeUtf8(descriptor)

    private var signature : String? = null
    val attributes: MutableList<FieldAttribute> = mutableListOf()

    fun _annotate(annotation: Class<*>) {
    }

    fun _signature(signature: String) {
        this.signature = signature
    }

    open fun flush() {
        if(signature != null) {
            attributes += SignatureAttribute(constantPool.writeUtf8("Signature"),
                constantPool.writeUtf8(signature!!))
        }
    }

    override fun toString(): String {
        return "${this::class.java.simpleName}(name='$name', descriptor='$descriptor', access=$access)"
    }


}