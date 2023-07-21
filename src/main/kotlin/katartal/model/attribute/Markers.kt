package katartal.model.attribute

interface ClassAttribute {
    fun toByteArray(): ByteArray
}

interface MethodAttribute {
    fun toByteArray(): ByteArray
}

interface MethodCodeAttribute {
    fun toByteArray(): ByteArray
}

interface FieldAttribute {
    fun toByteArray(): ByteArray
}