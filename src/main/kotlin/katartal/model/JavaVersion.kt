package katartal.model

import org.objectweb.asm.Opcodes

enum class JavaVersion(val opcode: Int) {
    V1(Opcodes.V1_1),
    V2(Opcodes.V1_2),
    V3(Opcodes.V1_3),
    V4(Opcodes.V1_4),
    V5(Opcodes.V1_5),
    V6(Opcodes.V1_6),
    V7(Opcodes.V1_7),
    V8(Opcodes.V1_8),
    V9(Opcodes.V9),
    V10(Opcodes.V10),
    V11(Opcodes.V11),
    V12(Opcodes.V12),
    V13(Opcodes.V13),
    V14(Opcodes.V14),
    V15(Opcodes.V15),
    V16(Opcodes.V16),
    V17(Opcodes.V17),
    V18(Opcodes.V18),
    V19(Opcodes.V19),
    V20(Opcodes.V20),
    V21(Opcodes.V21)
}