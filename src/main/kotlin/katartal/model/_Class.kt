package katartal.model

data class _Class(
    val name: String,
    val access: KlassAccess,
    val parent: String = "java/lang/Object",
    val implements: List<String> = listOf(),
    val methods: List<_Method>,
    val version: JavaVersion = JavaVersion.V8
) {
    fun <T> Class<T>.path(): String {
        val pkg = this.`package`
        return pkg.name.replace(".", "/") + "/" + this.simpleName
    }

    infix fun extends(parentCls: String): _Class {
        return copy(parent = parentCls)
    }

    infix fun <T : Any> extends(parentCls: Class<T>): _Class {
        return copy(parent = parentCls.path())
    }

    infix fun implements(interfaceCls: String): _Class {
        return copy(implements = this.implements + interfaceCls)
    }

    infix fun <T : Any> implements(interfaceCls: Class<T>): _Class {
        return copy(implements = this.implements + interfaceCls.path())
    }

    infix fun <T : Any> implements(interfaceClasses: List<String>): _Class {
        return copy(implements = this.implements + interfaceClasses)
    }

    infix fun <T : Any> implements(interfaceClasses: List<Class<T>>): _Class {
        return copy(implements = this.implements + interfaceClasses.map { it.path() })
    }
}

