package katartal.model

/**
 * Holder for index in Constant Pool
 */
@JvmInline
value class CPoolIndex(val index: UShort) {
    constructor(index: Int) : this(index.toUShort())

    override fun toString(): String = "$index"

    fun toInt(): Int = index.toInt()
    fun toUInt(): UInt = index.toUInt()
} 