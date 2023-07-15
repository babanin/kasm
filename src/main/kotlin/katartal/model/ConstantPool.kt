package katartal.model

import katartal.util.DynamicByteArray

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
        val nameIndex = writeUtf8(value)
        return addEntry(CONSTANT_Class_info(nameIndex))
    }

    fun writeNameAndType(name: String, type: String): UShort {
        val nameIndex = writeUtf8(name)
        val typeIndex = writeUtf8(type)
        return addEntry(CONSTANT_NameAndType(nameIndex, typeIndex))
    }

    fun writeMethodRef(cls: String, name: String, type: String): UShort {
        val clsIndex = writeClass(cls)
        val nameAndTypeIdx = writeNameAndType(name, type)
        return addEntry(CONSTANT_Methodref_info(clsIndex, nameAndTypeIdx))
    }

    fun writeFieldRef(cls: String, name: String, type: String): UShort {
        val clsIndex = writeClass(cls)
        val nameAndTypeIdx = writeNameAndType(name, type)
        return addEntry(CONSTANT_Fieldref_info(clsIndex, nameAndTypeIdx))
    }

    fun writeUtf8(value: String): UShort {
        if (compact) {
            entries.forEachIndexed { idx, it ->
                if (it.tag == Tag.CONSTANT_Utf8 && (it is CONSTANT_Utf8_info) && it.value == value) {
                    return@writeUtf8 (idx + 1).toUShort()
                }
            }
        }

        return addEntry(CONSTANT_Utf8_info(value))
    }

    fun writeString(value: String): UShort {
        return addEntry(CONSTANT_String_info(writeUtf8(value)))
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

    data class CONSTANT_Methodref_info(val clsIndex: UShort, val nameAndTypeIdx: UShort) :
        ConstantPoolEntry(Tag.CONSTANT_NameAndType) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray().apply {
                putU1(Tag.CONSTANT_Methodref.code)
                putU2(clsIndex.toUInt())
                putU2(nameAndTypeIdx.toUInt())
            }.toByteArray()
        }
    }

    data class CONSTANT_Fieldref_info(val clsIndex: UShort, val nameAndTypeIdx: UShort) :
        ConstantPoolEntry(Tag.CONSTANT_Fieldref) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray().apply {
                putU1(Tag.CONSTANT_Fieldref.code)
                putU2(clsIndex.toUInt())
                putU2(nameAndTypeIdx.toUInt())
            }.toByteArray()
        }
    }

    data class CONSTANT_NameAndType(val nameIndex: UShort, val typeIndex: UShort) :
        ConstantPoolEntry(Tag.CONSTANT_NameAndType) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray().apply {
                putU1(Tag.CONSTANT_NameAndType.code)
                putU2(nameIndex)
                putU2(typeIndex)
            }.toByteArray()
        }
    }

    data class CONSTANT_String_info(val stringIndex: UShort) : ConstantPoolEntry(Tag.CONSTANT_String) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray().apply {
                putU1(Tag.CONSTANT_String.code)
                putU2(stringIndex)
            }.toByteArray()
        }
    }

    data class CONSTANT_Class_info(val nameIndex: UShort) : ConstantPoolEntry(Tag.CONSTANT_Class) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray().apply {
                putU1(Tag.CONSTANT_Class.code)
                putU2(nameIndex)
            }.toByteArray()
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