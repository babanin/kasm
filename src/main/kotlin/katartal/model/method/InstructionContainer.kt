package katartal.model.method

import katartal.model.method.instruction.InstructionBuilder

abstract class InstructionContainer {
    abstract fun instructions() : List<InstructionBuilder>
}