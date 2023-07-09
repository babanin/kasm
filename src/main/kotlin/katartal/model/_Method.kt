package katartal.model

data class _Method(
    val access: MethodAccess,
    val name: String,
    val returns: String = Void::javaClass.toString(),
    val instructions: List<_Instruction> = listOf(),
    val throws: List<String> = listOf()
) {
    fun <T> Class<T>.path(): String {
        val pkg = this.`package`
        return pkg.name.replace(".", "/") + "/" + this.simpleName
    }

    infix fun throws(interfaceCls: String): _Method {
        return copy(throws = this.throws + interfaceCls)
    }

    infix fun <T : Any> throws(interfaceCls: Class<T>): _Method {
        return copy(throws = this.throws + interfaceCls.path())
    }

    infix fun <T : Any> throws(interfaceClasses: List<String>): _Method {
        return copy(throws = this.throws + interfaceClasses)
    }

    infix fun <T : Any> throws(interfaceClasses: List<Class<T>>): _Method {
        return copy(throws = this.throws + interfaceClasses.map { it.path() })
    }
}