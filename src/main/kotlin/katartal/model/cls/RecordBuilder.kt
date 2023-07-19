package katartal.model.cls

import katartal.util.path

class RecordBuilder(name: String, access: ClassAccess) :
    CommonClassBuilder<RecordBuilder>(name, access + ClassAccess.ENUM, "java/lang/Record") {
    infix fun implements(interfaceCls: String): RecordBuilder {
        this.implements += constantPool.writeClass(interfaceCls)
        return this
    }

    infix fun <T : Any> implements(interfaceCls: Class<T>): RecordBuilder {
        this.implements += constantPool.writeClass(interfaceCls.path())
        return this
    }
}