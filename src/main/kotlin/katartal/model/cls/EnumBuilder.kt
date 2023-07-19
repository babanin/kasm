package katartal.model.cls

import katartal.model.field.FieldAccess
import katartal.model.field.FieldAccess.Companion.ENUM
import katartal.model.field.FieldAccess.Companion.FINAL
import katartal.model.field.FieldAccess.Companion.PUBLIC
import katartal.model.field.FieldAccess.Companion.STATIC
import katartal.model.method.CodeBuilder
import katartal.model.method.MethodAccess
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
    
    fun _value(name :String, ctrCode: CodeBuilder.() -> Unit = {}) {
        _field(name, "L${name};", PUBLIC + STATIC + FINAL + ENUM)
    }
    
    override fun flush() {
        _method("<cinit>", access = MethodAccess.STATIC) {
            
        }
        
        super.flush()
    }
}