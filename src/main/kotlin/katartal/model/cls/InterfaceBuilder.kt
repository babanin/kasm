package katartal.model.cls

import katartal.util.path

class InterfaceBuilder(name: String, access: ClassAccess, parent: String = "java/lang/Object") :
    CommonClassBuilder<InterfaceBuilder>(name, access, parent) {

    infix fun extends(parentCls: String): InterfaceBuilder {
        this.parentClassNameIdx = constantPool.writeClass(parentCls)
        return this
    }

    infix fun <T : Any> extends(parentCls: Class<T>): InterfaceBuilder {
        this.parentClassNameIdx = constantPool.writeClass(parentCls.path())
        return this
    }
}