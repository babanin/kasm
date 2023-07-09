package katartal.model

class ClassBuilder(
    val name: String, var access: KlassAccess, var parent: String = "java/lang/Object"
) {
    private val methodBuilders: MutableList<MethodBuilder> = mutableListOf()

    fun constructor(parameters: List<Pair<String, Any>> = listOf(), init: MethodBuilder.() -> Unit): MethodBuilder {
        val methodBuilder = MethodBuilder(name, ctr = true)
        methodBuilders.add(methodBuilder)
        methodBuilder.init()
        return methodBuilder
    }

    fun method(
        name: String,
        parameters: List<Pair<String, Any>> = listOf(),
        returns: Any = Void::class.java,
        init: MethodBuilder.() -> Unit
    ): _Method {
        val methodBuilder = MethodBuilder(name)
        methodBuilders.add(methodBuilder)

        methodBuilder.init()
        return methodBuilder.build()
    }

    fun build(): _Class {
        val methods = methodBuilders.map { it.build() }.toMutableList()

        val ctrExists = methodBuilders.any { it.ctr }
        if (!ctrExists) {
            methods.add(defaultCtr());
        }

        return _Class(name, access, parent, emptyList(), methods)
    }

    private fun defaultCtr(): _Method {
        return method("<init>") {
            invokeSpecial(Object::class.java, "<init>", "()V")
        }
    }
}