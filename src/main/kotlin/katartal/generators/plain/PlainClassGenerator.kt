package katartal.generators.plain

import katartal.generators.ClassGenerator
import katartal.model.ClassBuilder

class PlainClassGenerator : ClassGenerator {
    private class DynamicByteArray {
        var store = ByteArray(1024)

        var pointer = 0

        private fun resizeIfNeeded(expected: Int) {
            if (pointer + expected >= store.size) {
                store = store.copyOf(store.size * 2)
            }
        }

        fun putU1(num: UByte) {
            resizeIfNeeded(1)

            store[pointer] = num.toByte()
            pointer += 1
        }

        fun putU2(short: UShort) {
            putU2(short.toUInt())
        }

        fun putU2(num: UInt) {
            resizeIfNeeded(2)

            store[pointer] = ((num shr 8) and 255u).toByte()
            pointer += 1
            store[pointer] = (num and 255u).toByte()
            pointer += 1
        }

        fun putU4(num: UInt) {
            resizeIfNeeded(4)

            store[pointer] = ((num shr 24) and 255u).toByte()
            pointer += 1
            store[pointer] = ((num shr 16) and 255u).toByte()
            pointer += 1
            store[pointer] = ((num shr 8) and 255u).toByte()
            pointer += 1
            store[pointer] = (num and 255u).toByte()
            pointer += 1
        }

        fun putByteArray(bytes: ByteArray) {
            resizeIfNeeded(bytes.size)
            bytes.copyInto(store, pointer)
            pointer += bytes.size
        }

        fun toByteArray(): ByteArray {
            return store.copyOf(pointer)
        }
    }

    /**
     * ClassFile {
     *     u4             magic;
     *     u2             minor_version;
     *     u2             major_version;
     *     u2             constant_pool_count;
     *     cp_info        constant_pool[constant_pool_count-1];
     *     u2             access_flags;
     *     u2             this_class;
     *     u2             super_class;
     *     u2             interfaces_count;
     *     u2             interfaces[interfaces_count];
     *     u2             fields_count;
     *     field_info     fields[fields_count];
     *     u2             methods_count;
     *     method_info    methods[methods_count];
     *     u2             attributes_count;
     *     attribute_info attributes[attributes_count];
     * }
     */
    override fun toByteArray(clsBuilder: ClassBuilder): ByteArray {
        val cls = DynamicByteArray()
        cls.putU4(0xCAFEBABEu)
        cls.putU2(0u) // minor version
        cls.putU2(clsBuilder.version.opcode) // major version

        cls.putU2((clsBuilder.constantPool.size + 1).toUInt()) // constant pool size
        for (entry in clsBuilder.constantPool) {
            cls.putByteArray(entry.toByteArray())
        }

        cls.putU2(clsBuilder.access.opcode) // access flags

        cls.putU2(clsBuilder.classNameIdx)// this class
        cls.putU2(clsBuilder.parentClassNameIdx) // super class

        cls.putU2(clsBuilder.implements.size.toUInt()) // interfaces_count
        for (interfaceIdx in clsBuilder.implements) { // interfaces
            cls.putU2(interfaceIdx) // super class
        }

        cls.putU2(0u) // fields_count
        // fields

        cls.putU2(0u) // methods_count
        // methods

        cls.putU2(0u) // attributes_count
        // attributes

        return cls.toByteArray()
    }
}