package katartal.util

fun <T> Class<T>.path(): String {
    val pkg = this.`package`
    return pkg.name.replace(".", "/") + "/" + this.simpleName
}

fun <T> Class<T>.descriptor(): String {
    if (this == Void::class.java) {
        return "V"
    }

    return descriptorString()
}

fun methodDescriptor(parameters: List<Class<*>>, returnType: Class<*>): String {
    return parameters.joinToString(
        separator = "",
        prefix = "(",
        postfix = ")"
    ) { it.descriptor() } + returnType.descriptor()
}