package katartal.util

fun <T> Class<T>.path(): String {
    val pkg = this.`package`
    return pkg.name.replace(".", "/") + "/" + this.simpleName
}

private val PRIMITIVES = mapOf<Class<*>, String>(
    Byte::class.java to "B",
    java.lang.Byte::class.java to "B",
    
    Char::class.java to "C",
    java.lang.Character::class.java to "C",
    
    Double::class.java to "D",
    java.lang.Double::class.java to "D",
    
    Float::class.java to "F",
    java.lang.Float::class.java to "F",
    
    Int::class.java to "I",
    java.lang.Integer::class.java to "I",
    
    Long::class.java to "J",
    java.lang.Long::class.java to "J",
    
    Void::class.java to "V",
    
    Boolean::class.java to "Z",
    java.lang.Boolean::class.java to "Z"
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
    }

    return "L" + path() + ";"
}