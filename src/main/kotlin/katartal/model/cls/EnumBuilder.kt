package katartal.model.cls

import katartal.model.method.CodeBuilder
import katartal.util.path

class EnumBuilder(name: String, access: ClassAccess) : CommonClassBuilder<EnumBuilder>(name, access + ClassAccess.ENUM, "java/lang/Enum") {
    infix fun implements(interfaceCls: String): EnumBuilder {
        this.implements += constantPool.writeClass(interfaceCls)
        return this
    }

    infix fun <T : Any> implements(interfaceCls: Class<T>): EnumBuilder {
        this.implements += constantPool.writeClass(interfaceCls.path())
        return this
    }
    
    fun _value(name :String, ctrCode: CodeBuilder.() -> Unit) {
        
    }
}