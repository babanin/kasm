package katartal.util

fun <T> Class<T>.path(): String {
    val pkg = this.`package`
    return pkg.name.replace(".", "/") + "/" + this.simpleName
}

private val PRIMITIVES = mapOf<Class<*>, String>(
    Byte::class.java to "B",
    Boolean::class.java to "Z",
    Char::class.java to "C",
    Double::class.java to "D",
    Float::class.java to "F",
    Int::class.java to "I",
    Long::class.java to "J"
)

fun <T> Class<T>.descriptor(): String {
    if (isPrimitive) {
        return PRIMITIVES[this]!!
    } else if (isArray) {
        val sb = StringBuilder()

        var cls: Class<*> = this
        while (cls.isArray) {
            sb.append("[")
            cls = cls.componentType
        }

        if (cls.isPrimitive) {
            sb.append(PRIMITIVES[cls])
        } else {
            sb.append("L")
                .append(cls.path())
                .append(";")
        }

        return sb.toString()
    } else if (this == Void::class.java) {
        return "V"
    }

    return "L" + path() + ";"
}