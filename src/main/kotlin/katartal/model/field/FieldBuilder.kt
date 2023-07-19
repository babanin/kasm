package katartal.model.field

import katartal.model.CPoolIndex
import katartal.model.ConstantPool

class FieldBuilder(name : String, descriptor: String, val access: FieldAccess, private val constantPool : ConstantPool) {
    
    val nameCpIndex : CPoolIndex
    val descriptorCpIndex : CPoolIndex
    
    init {
        this.nameCpIndex = constantPool.writeUtf8(name)
        this.descriptorCpIndex = constantPool.writeUtf8(descriptor)
    }
    
    fun flush() {
        
    }
}