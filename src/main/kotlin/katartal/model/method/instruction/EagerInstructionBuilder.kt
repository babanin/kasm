package katartal.model.method.instruction

import katartal.model.ByteCode
import katartal.model.method.Instruction

class EagerInstructionBuilder internal constructor(code: ByteCode) : InstructionBuilder(code) {
    override fun flush(): Instruction {
        if (code.expectedParameters != 0u.toUShort() && operands.size.toUShort() != code.expectedParameters) {
            throw IllegalStateException("Number of operands (${operands.size}) differs from expected " +
                    "(${code.expectedParameters}) for bytecode $code")
        }

        return Instruction(code, operands)
    }
}
