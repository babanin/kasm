package katartal.util

fun <T> Class<T>.path(): String {
    val pkg = this.`package`
    return pkg.name.replace(".", "/") + "/" + this.simpleName
}

private val PRIMITIVES = mapOf<Class<*>, String>(
    Integer::class.java to "I",
    Boolean::class.java to "Z",
    Character::class.java to "C",
    Long::class.java to "L",
    Float::class.java to "F",
    Double::class.java to "D"
)

fun <T> Class<T>.descriptor(): String {
    if (isPrimitive) {
        return PRIMITIVES[this]!!
    } else if (isArray) {

    } else {
    }

    return "L" + path()
}