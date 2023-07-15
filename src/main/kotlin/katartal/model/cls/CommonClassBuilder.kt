package katartal.model.cls

import katartal.model.ConstantPool
import katartal.model.FieldBuilder
import katartal.model.JavaVersion
import katartal.model.method.MethodAccess
import katartal.model.method.MethodBuilder

abstract class CommonClassBuilder<SELF : CommonClassBuilder<SELF>>(name: String, var access: ClassAccess, parent: String = "java/lang/Object") {
    val name: String
        get() = constantPool.readClass(classNameIdx)!!

    val parent: String
        get() = constantPool.readClass(parentClassNameIdx)!!

    var implements: MutableList<UShort> = mutableListOf()
    val version: JavaVersion = JavaVersion.V17

    val fieldBuilders: MutableList<FieldBuilder> = mutableListOf()
    val methodBuilders: MutableList<MethodBuilder> = mutableListOf()

    val classNameIdx: UShort
    var parentClassNameIdx: UShort

    val constantPool = ConstantPool()

    init {
        this.classNameIdx = constantPool.writeClass(name)
        this.parentClassNameIdx = constantPool.writeClass(parent)
    }

    fun _constructor(parameters: List<Pair<String, Any>> = listOf(), init: MethodBuilder.() -> Unit): MethodBuilder {
        val methodBuilder = MethodBuilder(ctr = true, parameters = parameters, constantPool = constantPool)
        methodBuilders.add(methodBuilder)
        methodBuilder.init()
        return methodBuilder
    }

    fun _method(
        name: String,
        parameters: List<Pair<String, Any>> = listOf(),
        access: MethodAccess = MethodAccess.PUBLIC,
        init: MethodBuilder.() -> Unit
    ): MethodBuilder {
        val methodBuilder = MethodBuilder(name, access, parameters = parameters, constantPool = constantPool)
        methodBuilders.add(methodBuilder)

        methodBuilder.init()
        return methodBuilder
    }

    fun flush() {
        methodBuilders.forEach { it.flush() }
        fieldBuilders.forEach { it.flush() }
    }
}