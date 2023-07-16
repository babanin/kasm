package katartal.model

@JvmInline
value class CPoolIndex(val index: UShort) {
    constructor(index: Short) : this(index.toUShort())
    constructor(index: Int) : this(index.toUShort())
} 