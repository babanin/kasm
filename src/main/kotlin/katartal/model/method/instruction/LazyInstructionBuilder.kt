package katartal.model.method.instruction

import katartal.model.ByteCode

class LazyInstructionBuilder internal constructor(
    code: ByteCode,
    private val reserve: Int,
    private val evaluator: LazyInstructionBuilder.() -> Unit
) :
    InstructionBuilder(code) {

    override val size: Int
        get() = 1 + reserve

    override fun flush() {
        this.evaluator()

        if (operands.size != reserve) {
            throw IllegalStateException(
                "Evaluated operand size (${operands.size}) differs from expected ($reserve). " +
                        "Branch offset instructions might drift."
            )
        }
    }
}
