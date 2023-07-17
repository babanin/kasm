package katartal.model

@JvmInline
value class CPoolIndex(val index: UShort) {
    constructor(index: Int) : this(index.toUShort())

    override fun toString(): String = "$index"


} 