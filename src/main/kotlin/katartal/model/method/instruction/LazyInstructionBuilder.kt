package katartal.model.method.instruction

import katartal.model.ByteCode
import katartal.model.method.Instruction

class LazyInstructionBuilder internal constructor(
    code: ByteCode,
    private val reserve: UShort,
    private val evaluator: LazyInstructionBuilder.() -> Unit
) :
    InstructionBuilder(code) {

    override val size: UShort
        get() = (1u + reserve).toUShort()

    override fun flush() : Instruction {
        this.evaluator()

        if (operands.size.toUShort() != reserve) {
            throw IllegalStateException(
                "Evaluated operand size (${operands.size}) differs from expected ($reserve). " +
                        "Branch offset instructions might drift."
            )
        }
        
        return Instruction(code, operands)
    }
}
