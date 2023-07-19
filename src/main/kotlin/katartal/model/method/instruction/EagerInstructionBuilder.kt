package katartal.model.method.instruction

import katartal.model.ByteCode

class EagerInstructionBuilder internal constructor(code: ByteCode) : InstructionBuilder(code) {
    override fun flush() {
        if (code.expectedParameters != 0 && operands.size != code.expectedParameters) {
            throw IllegalStateException("Number of operands (${operands.size}) differs from expected (${code.expectedParameters})")
        }
    }
}
