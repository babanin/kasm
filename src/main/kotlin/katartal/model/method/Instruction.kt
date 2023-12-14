package katartal.model.method

import katartal.model.ByteCode

data class Instruction(val code: ByteCode, val operands: List<UByte>) 