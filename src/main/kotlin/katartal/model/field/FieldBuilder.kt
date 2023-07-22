package katartal.model.field

import katartal.model.CPoolIndex
import katartal.model.ConstantPool
import katartal.model.attribute.FieldAttribute

open class FieldBuilder(
    val name: String,
    val descriptor: String,
    val access: FieldAccess,
    constantPool: ConstantPool
) {
    val nameCpIndex: CPoolIndex = constantPool.writeUtf8(name)
    val descriptorCpIndex: CPoolIndex = constantPool.writeUtf8(descriptor)

    val attributes: MutableList<FieldAttribute> = mutableListOf()

    fun _annotate(annotation: Class<*>) {
    }

    open fun flush() {

    }

    override fun toString(): String {
        return "${this::class.java.simpleName}(name='$name', descriptor='$descriptor', access=$access)"
    }


}