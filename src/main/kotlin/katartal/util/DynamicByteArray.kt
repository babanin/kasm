package katartal.util

/**
 * Big-endian dynamic byte array
 */
class DynamicByteArray(preallocate: Int = 1024) {
    private var store: ByteArray
    private var pointer = 0

    init {
        store = ByteArray(preallocate)
    }

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

    fun putU2(short: UShort): DynamicByteArray {
        return putU2(short.toUInt())
    }

    fun putU2(int: Int): DynamicByteArray {
        return putU2(int.toUInt())
    }

    fun putU2(num: UInt): DynamicByteArray {
        resizeIfNeeded(2)

        store[pointer] = ((num shr 8) and 255u).toByte()
        pointer += 1
        store[pointer] = (num and 255u).toByte()
        pointer += 1

        return this
    }

    fun putU4(int: Int) {
        putU4(int.toUInt())
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

    fun putU8(num: Long) {
        resizeIfNeeded(8)

        store[pointer] = ((num shr 56) and 255).toByte()
        pointer += 1
        store[pointer] = ((num shr 48) and 255).toByte()
        pointer += 1
        store[pointer] = ((num shr 40) and 255).toByte()
        pointer += 1
        store[pointer] = ((num shr 32) and 255).toByte()
        pointer += 1
        store[pointer] = ((num shr 24) and 255).toByte()
        pointer += 1
        store[pointer] = ((num shr 16) and 255).toByte()
        pointer += 1
        store[pointer] = ((num shr 8) and 255).toByte()
        pointer += 1
        store[pointer] = (num and 255).toByte()
        pointer += 1
    }

    fun putByteArray(bytes: ByteArray) {
        resizeIfNeeded(bytes.size)
        bytes.copyInto(store, pointer)
        pointer += bytes.size
    }

    fun putByteArray(bytes: DynamicByteArray) {
        resizeIfNeeded(bytes.size)
        bytes.store.copyInto(this.store, pointer, 0, bytes.pointer)
        pointer += bytes.size
    }

    val size: Int
        get() = pointer

    fun toByteArray(): ByteArray {
        return store.copyOf(pointer)
    }
}