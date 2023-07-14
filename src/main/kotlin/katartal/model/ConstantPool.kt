package katartal.model

@Suppress("ClassName")
class ConstantPool(val compact: Boolean = true) : Iterable<ConstantPool.ConstantPoolEntry> {
    private val entries: MutableList<ConstantPoolEntry> = mutableListOf()

    enum class Tag(val code: UByte) {
        CONSTANT_Utf8(1u),
        CONSTANT_Integer(3u),
        CONSTANT_Float(4u),
        CONSTANT_Long(5u),
        CONSTANT_Double(6u),
        CONSTANT_Class(7u),
        CONSTANT_String(8u),
        CONSTANT_Fieldref(9u),
        CONSTANT_Methodref(10u),
        CONSTANT_InterfaceMethodref(11u),
        CONSTANT_NameAndType(12u),
        CONSTANT_MethodHandle(15u),
        CONSTANT_MethodType(16u),
        CONSTANT_Dynamic(17u),
        CONSTANT_InvokeDynamic(18u),
        CONSTANT_Module(19u),
        CONSTANT_Package(20u)
    }

    fun writeClass(value: String): UShort {
        val idx = writeUtf8(value)
        return addEntry(CONSTANT_Class_info(idx))
    }

    fun writeUtf8(value: String): UShort {
        if (compact) {
            entries.forEachIndexed { idx, it ->
                if (it.tag == Tag.CONSTANT_Utf8 && (it is CONSTANT_Utf8_info) && it.value == value) {
                    return@writeUtf8 idx.toUShort()
                }
            }
        }

        return addEntry(CONSTANT_Utf8_info(value))
    }

    fun readUtf8(idx: UShort): String? {
        val utf8Info = entries[idx.toInt() - 1]
        if (utf8Info !is CONSTANT_Utf8_info) return null

        return utf8Info.value
    }

    fun readClass(idx: UShort): String? {
        val classInfo = entries[idx.toInt() - 1]
        if (classInfo !is CONSTANT_Class_info) return null

        return readUtf8(classInfo.nameIndex)
    }

    private fun addEntry(entry: ConstantPoolEntry): UShort {
        entries.add(entry)
        return entries.size.toUShort()
    }

    sealed class ConstantPoolEntry(val tag: Tag) {
        abstract fun toByteArray(): ByteArray
    }

    data class CONSTANT_Class_info(val nameIndex: UShort) : ConstantPoolEntry(Tag.CONSTANT_Class) {
        override fun toByteArray(): ByteArray {
            val idx = nameIndex.toUInt()

            val res = ByteArray(1 + 2)
            res[0] = Tag.CONSTANT_Class.code.toByte()
            res[1] = (idx shr 8 and 255u).toByte()
            res[2] = (nameIndex and 255u).toByte()

            return res
        }
    }

    data class CONSTANT_Utf8_info(val value: String) : ConstantPoolEntry(Tag.CONSTANT_Utf8) {
        override fun toByteArray(): ByteArray {
            val charBytes = value.toByteArray(Charsets.UTF_8)

            val res = ByteArray(1 + 2 + charBytes.size)
            res[0] = Tag.CONSTANT_Utf8.code.toByte()
            res[1] = (charBytes.size shr 8 and 255).toByte()
            res[2] = (charBytes.size and 255).toByte()

            charBytes.copyInto(res, 3)

            return res
        }

    }

    val size get() = entries.size
        
    override fun iterator(): Iterator<ConstantPoolEntry> = entries.iterator()
}