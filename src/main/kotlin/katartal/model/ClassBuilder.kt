package katartal.model

class ClassBuilder(name: String, var access: ClassAccess, parent : String = "java/lang/Object") {
    val name: String
        get() = constantPool.readClass(classNameIdx)!!

    val parent: String 
        get() = constantPool.readClass(parentClassNameIdx)!!
    
    var implements: MutableList<UShort> = mutableListOf()
    val version: JavaVersion = JavaVersion.V17
    
    private val fieldBuilders: MutableList<FieldBuilder> = mutableListOf()
    private val methodBuilders: MutableList<MethodBuilder> = mutableListOf()
    
    val classNameIdx : UShort
    var parentClassNameIdx : UShort
    
    val constantPool = ConstantPool()

    init {
        this.classNameIdx = constantPool.writeClass(name)
        this.parentClassNameIdx = constantPool.writeClass(parent)
    }

    fun _constructor(parameters: List<Pair<String, Any>> = listOf(), init: MethodBuilder.() -> Unit): MethodBuilder {
        val methodBuilder = MethodBuilder(name, ctr = true)
        methodBuilders.add(methodBuilder)
        methodBuilder.init()
        return methodBuilder
    }

    fun _method(
        name: String,
        parameters: List<Pair<String, Any>> = listOf(),
        returns: Any = Void::class.java,
        init: MethodBuilder.() -> Unit
    ): MethodBuilder {
        val methodBuilder = MethodBuilder(name)
        methodBuilders.add(methodBuilder)

        methodBuilder.init()
        return methodBuilder
    }

    private fun defaultCtr(): MethodBuilder {
        return _method("<init>") {
            invokeSpecial(Object::class.java, "<init>", "()V")
        }
    }

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

    fun <T> Class<T>.path(): String {
        val pkg = this.`package`
        return pkg.name.replace(".", "/") + "/" + this.simpleName
    }
}