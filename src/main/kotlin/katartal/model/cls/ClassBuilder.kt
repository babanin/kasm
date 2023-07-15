package katartal.model.cls

import katartal.util.path

class ClassBuilder(name: String, access: ClassAccess, parent: String = "java/lang/Object") :
    CommonClassBuilder<ClassBuilder>(name, access, parent) {

    infix fun extends(parentCls: String): ClassBuilder {
        this.parentClassNameIdx = constantPool.writeClass(parentCls)
        return this
    }

    infix fun <T : Any> extends(parentCls: Class<T>): ClassBuilder {
        this.parentClassNameIdx = constantPool.writeClass(parentCls.path())
        return this
    }

    infix fun implements(interfaceCls: String): ClassBuilder {
        this.implements += constantPool.writeClass(interfaceCls)
        return this
    }

    infix fun <T : Any> implements(interfaceCls: Class<T>): ClassBuilder {
        this.implements += constantPool.writeClass(interfaceCls.path())
        return this
    }
}