package katartal.model

/**
 * Holder for index in Constant Pool
 */
@JvmInline
value class CPoolIndex(val index: UShort) {
    constructor(index: Int) : this(index.toUShort())

    override fun toString(): String = "$index"

    operator fun component1(): UByte {
        return (index.toInt() shr 8 and 255).toUByte()
    }

    operator fun component2(): UByte {
        return (index.toInt() and 255).toUByte()
    }

    fun toInt(): Int = index.toInt()
    fun toUInt(): UInt = index.toUInt()
} 