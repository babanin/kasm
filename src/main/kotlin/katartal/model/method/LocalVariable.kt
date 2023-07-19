package katartal.model.method

import katartal.model.CPoolIndex

data class LocalVariable(
    val nameIndex: CPoolIndex,
    val startPc: UShort,
    val length: UShort,
    val descriptor: CPoolIndex
)