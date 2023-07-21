package katartal.model.cls

import katartal.model.BoostrapMethodBuilder
import katartal.model.CPoolIndex
import katartal.model.ConstantPool
import katartal.model.JavaVersion
import katartal.model.attribute.BootstrapMethod
import katartal.model.attribute.BootstrapMethodAttribute
import katartal.model.attribute.ClassAttribute
import katartal.model.field.FieldAccess
import katartal.model.field.FieldBuilder
import katartal.model.method.MethodAccess
import katartal.model.method.MethodBuilder
import katartal.util.descriptor
import katartal.util.path

abstract class CommonClassBuilder<SELF : CommonClassBuilder<SELF>>(
    val className: String,
    var access: ClassAccess,
    parent: String = Object::class.java.path()
) {
    val parent: String
        get() = constantPool.readClass(parentClassNameIdx)!!

    var implements: MutableList<CPoolIndex> = mutableListOf()
    val version: JavaVersion = JavaVersion.V17

    val fieldBuilders: MutableList<FieldBuilder> = mutableListOf()
    val methodBuilders: MutableList<MethodBuilder> = mutableListOf()
    val boostrapMethodBuilders: MutableList<BoostrapMethodBuilder> = mutableListOf()

    val classNameIdx: CPoolIndex
    var parentClassNameIdx: CPoolIndex

    val constantPool = ConstantPool()

    val attributes: MutableList<ClassAttribute> = mutableListOf()

    init {
        this.classNameIdx = constantPool.writeClass(className)
        this.parentClassNameIdx = constantPool.writeClass(parent)
    }

    fun _constructor(
        parameters: List<Pair<String, Any>> = listOf(),
        access: MethodAccess = MethodAccess.PUBLIC,
        init: MethodBuilder.() -> Unit
    ): MethodBuilder {
        val methodBuilder =
            MethodBuilder(
                ctr = true,
                parameters = parameters,
                constantPool = constantPool,
                currentClass = className,
                access = access
            )
        methodBuilders.add(methodBuilder)
        methodBuilder.init()
        return methodBuilder
    }

    fun _method(
        name: String,
        parameters: List<Pair<String, Any>> = listOf(),
        access: MethodAccess = MethodAccess.PUBLIC,
        init: MethodBuilder.() -> Unit = {}
    ): MethodBuilder {
        val methodBuilder =
            MethodBuilder(name, access, parameters = parameters, constantPool = constantPool, currentClass = name)
        methodBuilders.add(methodBuilder)

        methodBuilder.init()
        return methodBuilder
    }

    fun _field(
        name: String,
        descriptor: Class<*>,
        access: FieldAccess = FieldAccess.PUBLIC,
        init: FieldBuilder.() -> Unit = {}
    ) {
        _field(name, descriptor.descriptor(), access, init)
    }

    fun _field(
        name: String,
        descriptor: String,
        access: FieldAccess = FieldAccess.PUBLIC,
        init: FieldBuilder.() -> Unit = {}
    ): FieldBuilder {
        val fieldBuilder = FieldBuilder(name, descriptor, access, constantPool)
        fieldBuilders += fieldBuilder
        fieldBuilder.init()
        return fieldBuilder
    }

    fun _bootstrapMethods(
        kind: ConstantPool.RefKind,
        cls: String,
        name: String,
        type: String,
        init: BoostrapMethodBuilder.() -> Unit
    ): BoostrapMethodBuilder {
        val boostrapMethodBuilder = BoostrapMethodBuilder(kind, cls, name, type, boostrapMethodBuilders.size.toUShort())
        boostrapMethodBuilders += boostrapMethodBuilder
        boostrapMethodBuilder.init()
        return boostrapMethodBuilder
    }

    fun _annotate(annotation: Class<*>) {

    }

    open fun flush() {
        methodBuilders.forEach { it.flush() }
        fieldBuilders.forEach { it.flush() }

        attributes += buildBootstrapMethodAttribute()

        println("Constant pool:")
        for ((idx, entry) in constantPool.withIndex()) {
            println("\t ${idx + 1} \t $entry")
        }
    }

    private fun buildBootstrapMethodAttribute(): BootstrapMethodAttribute {
        val methods = boostrapMethodBuilders.map {
            val bootstrapMethodRef =
                constantPool.writeMethodHandle(it.kind, constantPool.writeMethodRef(it.cls, it.name, it.type))

            val args = it.args.map { arg ->
                when (arg) {
                    is BoostrapMethodBuilder.Cls -> constantPool.writeClass(arg.cls)
                    is BoostrapMethodBuilder.MH -> constantPool.writeMethodHandle(
                        arg.refKind,
                        arg.cls,
                        arg.name,
                        arg.type
                    )

                    is BoostrapMethodBuilder.Str -> constantPool.writeString(arg.value)
                }
            }

            BootstrapMethod(bootstrapMethodRef, args)
        }

        return BootstrapMethodAttribute(constantPool.writeUtf8("BootstrapMethods"), methods)
    }
}