package katartal.model

import katartal.util.DynamicByteArray

@Suppress("ClassName")
class ConstantPool : Iterable<ConstantPool.ConstantPoolEntry> {
    private val entries: MutableList<ConstantPoolEntry> = mutableListOf()
    private val cache: MutableMap<ConstantPoolEntry, CPoolIndex> = mutableMapOf()

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

    fun writeClass(value: String): CPoolIndex {
        val nameIndex = writeUtf8(value)
        return addEntry(CONSTANT_Class_info(nameIndex))
    }

    fun writeNameAndType(name: String, type: String): CPoolIndex {
        val nameIndex = writeUtf8(name)
        val typeIndex = writeUtf8(type)
        return addEntry(CONSTANT_NameAndType(nameIndex, typeIndex))
    }

    fun writeMethodRef(cls: String, name: String, type: String): CPoolIndex {
        val clsIndex = writeClass(cls)
        val nameAndTypeIdx = writeNameAndType(name, type)
        return addEntry(CONSTANT_Methodref_info(clsIndex, nameAndTypeIdx))
    }

    fun writeFieldRef(cls: String, name: String, type: String): CPoolIndex {
        val clsIndex = writeClass(cls)
        val nameAndTypeIdx = writeNameAndType(name, type)
        return addEntry(CONSTANT_Fieldref_info(clsIndex, nameAndTypeIdx))
    }

    fun writeUtf8(value: String): CPoolIndex {
        return addEntry(CONSTANT_Utf8_info(value))
    }

    fun writeString(value: String): CPoolIndex {
        return addEntry(CONSTANT_String_info(writeUtf8(value)))
    }

    fun writeInteger(value: Int): CPoolIndex {
        return addEntry(CONSTANT_Integer_info(value))
    }

    fun writeFloat(value: Float): CPoolIndex {
        return addEntry(CONSTANT_Float_info(value))
    }

    fun writeLong(value: Long): CPoolIndex {
        return addEntry(CONSTANT_Long_info(value))
    }

    fun writeDouble(value: Double): CPoolIndex {
        return addEntry(CONSTANT_Double_info(value))
    }
    
    fun writeInterfaceMethodRef(cls: String, name: String, type: String) : CPoolIndex {
        val clsIndex = writeClass(cls)
        val nameAndTypeIdx = writeNameAndType(name, type)
        return addEntry(CONSTANT_InterfaceMethodref(clsIndex, nameAndTypeIdx))
    }

    fun readUtf8(idx: CPoolIndex): String? {
        val utf8Info = entries[idx.toInt() - 1]
        if (utf8Info !is CONSTANT_Utf8_info) return null

        return utf8Info.value
    }

    fun readClass(idx: CPoolIndex): String? {
        val classInfo = entries[idx.toInt() - 1]
        if (classInfo !is CONSTANT_Class_info) return null

        return readUtf8(classInfo.nameIndex)
    }

    private fun addEntry(entry: ConstantPoolEntry): CPoolIndex {
        return cache.computeIfAbsent(entry) { e ->
            entries.add(e)
            CPoolIndex(entries.size)
        }
    }

    sealed class ConstantPoolEntry(val tag: Tag) {
        abstract fun toByteArray(): ByteArray
    }

    data class CONSTANT_Methodref_info(val clsIndex: CPoolIndex, val nameAndTypeIdx: CPoolIndex) :
        ConstantPoolEntry(Tag.CONSTANT_NameAndType) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray().apply {
                putU1(Tag.CONSTANT_Methodref.code)
                putU2(clsIndex.toUInt())
                putU2(nameAndTypeIdx.toUInt())
            }.toByteArray()
        }
    }

    data class CONSTANT_Fieldref_info(val clsIndex: CPoolIndex, val nameAndTypeIdx: CPoolIndex) :
        ConstantPoolEntry(Tag.CONSTANT_Fieldref) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray().apply {
                putU1(Tag.CONSTANT_Fieldref.code)
                putU2(clsIndex.toUInt())
                putU2(nameAndTypeIdx.toUInt())
            }.toByteArray()
        }
    }

    data class CONSTANT_NameAndType(val nameIndex: CPoolIndex, val typeIndex: CPoolIndex) :
        ConstantPoolEntry(Tag.CONSTANT_NameAndType) {
        override fun toByteArray(): ByteArray =
            DynamicByteArray().apply {
                putU1(Tag.CONSTANT_NameAndType.code)
                putU2(nameIndex.toInt())
                putU2(typeIndex.toInt())
            }.toByteArray()
    }

    data class CONSTANT_String_info(val stringIndex: CPoolIndex) : ConstantPoolEntry(Tag.CONSTANT_String) {
        override fun toByteArray(): ByteArray =
            DynamicByteArray().apply {
                putU1(Tag.CONSTANT_String.code)
                putU2(stringIndex.toInt())
            }.toByteArray()
    }

    data class CONSTANT_Class_info(val nameIndex: CPoolIndex) : ConstantPoolEntry(Tag.CONSTANT_Class) {
        override fun toByteArray(): ByteArray =
            DynamicByteArray().apply {
                putU1(Tag.CONSTANT_Class.code)
                putU2(nameIndex.toInt())
            }.toByteArray()
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

    data class CONSTANT_InterfaceMethodref(val classIndex: CPoolIndex, val nameAndTypeIndex: CPoolIndex) :
        ConstantPoolEntry(Tag.CONSTANT_InterfaceMethodref) {
        override fun toByteArray(): ByteArray =
            DynamicByteArray().apply {
                putU1(Tag.CONSTANT_InterfaceMethodref.code)
                putU2(classIndex.toInt())
                putU2(nameAndTypeIndex.toInt())
            }.toByteArray()
    }

    data class CONSTANT_Integer_info(val value: Int) : ConstantPoolEntry(Tag.CONSTANT_Integer) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray(5).apply {
                putU1(Tag.CONSTANT_Integer.code)
                putU4(value)
            }.toByteArray()
        }

    }

    data class CONSTANT_Float_info(val value: Float) : ConstantPoolEntry(Tag.CONSTANT_Float) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray(5).apply {
                putU1(Tag.CONSTANT_Float.code)
                putU4(value.toBits())
            }.toByteArray()
        }

    }

    data class CONSTANT_Long_info(val value: Long) : ConstantPoolEntry(Tag.CONSTANT_Long) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray(5).apply {
                putU1(Tag.CONSTANT_Long.code)
                putU8(value)
            }.toByteArray()
        }
    }

    data class CONSTANT_Double_info(val value: Double) : ConstantPoolEntry(Tag.CONSTANT_Double) {
        override fun toByteArray(): ByteArray {
            return DynamicByteArray(5).apply {
                putU1(Tag.CONSTANT_Double.code)
                putU8(value.toBits())
            }.toByteArray()
        }
    }

    val size get() = entries.size

    override fun iterator(): Iterator<ConstantPoolEntry> = entries.iterator()
}