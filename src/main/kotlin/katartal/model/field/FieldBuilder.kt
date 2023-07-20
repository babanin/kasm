package katartal.model.field

import katartal.model.CPoolIndex
import katartal.model.ConstantPool
import katartal.model.attribute.Attribute

class FieldBuilder(
    val name: String,
    val descriptor: String,
    val access: FieldAccess,
    private val constantPool: ConstantPool
) {

    val nameCpIndex: CPoolIndex
    val descriptorCpIndex: CPoolIndex
    
    val attributes: MutableList<Attribute> = mutableListOf()

    init {
        this.nameCpIndex = constantPool.writeUtf8(name)
        this.descriptorCpIndex = constantPool.writeUtf8(descriptor)
    }

    fun flush() {

    }
}