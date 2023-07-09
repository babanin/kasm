package katartal.model

@JvmInline
value class ByteCode(val code: Int) {
    companion object {
        // The JVM opcode values (with the MethodVisitor method name used to visit them in comment, and
        // where '-' means 'same method name as on the previous line').
        // See https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html.
        val NOP = ByteCode(0) // visitInsn

        val ACONST_NULL = ByteCode(1) // -

        val ICONST_M1 = ByteCode(2) // -

        val ICONST_0 = ByteCode(3) // -

        val ICONST_1 = ByteCode(4) // -

        val ICONST_2 = ByteCode(5) // -

        val ICONST_3 = ByteCode(6) // -

        val ICONST_4 = ByteCode(7) // -

        val ICONST_5 = ByteCode(8) // -

        val LCONST_0 = ByteCode(9) // -

        val LCONST_1 = ByteCode(10) // -

        val FCONST_0 = ByteCode(11) // -

        val FCONST_1 = ByteCode(12) // -

        val FCONST_2 = ByteCode(13) // -

        val DCONST_0 = ByteCode(14) // -

        val DCONST_1 = ByteCode(15) // -

        val BIPUSH = ByteCode(16) // visitIntInsn

        val SIPUSH = ByteCode(17) // -

        val LDC = ByteCode(18) // visitLdcInsn

        val ILOAD = ByteCode(21) // visitVarInsn

        val LLOAD = ByteCode(22) // -

        val FLOAD = ByteCode(23) // -

        val DLOAD = ByteCode(24) // -

        val ALOAD = ByteCode(25) // -

        val IALOAD = ByteCode(46) // visitInsn

        val LALOAD = ByteCode(47) // -

        val FALOAD = ByteCode(48) // -

        val DALOAD = ByteCode(49) // -

        val AALOAD = ByteCode(50) // -

        val BALOAD = ByteCode(51) // -

        val CALOAD = ByteCode(52) // -

        val SALOAD = ByteCode(53) // -

        val ISTORE = ByteCode(54) // visitVarInsn

        val LSTORE = ByteCode(55) // -

        val FSTORE = ByteCode(56) // -

        val DSTORE = ByteCode(57) // -

        val ASTORE = ByteCode(58) // -

        val IASTORE = ByteCode(79) // visitInsn

        val LASTORE = ByteCode(80) // -

        val FASTORE = ByteCode(81) // -

        val DASTORE = ByteCode(82) // -

        val AASTORE = ByteCode(83) // -

        val BASTORE = ByteCode(84) // -

        val CASTORE = ByteCode(85) // -

        val SASTORE = ByteCode(86) // -

        val POP = ByteCode(87) // -

        val POP2 = ByteCode(88) // -

        val DUP = ByteCode(89) // -

        val DUP_X1 = ByteCode(90) // -

        val DUP_X2 = ByteCode(91) // -

        val DUP2 = ByteCode(92) // -

        val DUP2_X1 = ByteCode(93) // -

        val DUP2_X2 = ByteCode(94) // -

        val SWAP = ByteCode(95) // -

        val IADD = ByteCode(96) // -

        val LADD = ByteCode(97) // -

        val FADD = ByteCode(98) // -

        val DADD = ByteCode(99) // -

        val ISUB = ByteCode(100) // -

        val LSUB = ByteCode(101) // -

        val FSUB = ByteCode(102) // -

        val DSUB = ByteCode(103) // -

        val IMUL = ByteCode(104) // -

        val LMUL = ByteCode(105) // -

        val FMUL = ByteCode(106) // -

        val DMUL = ByteCode(107) // -

        val IDIV = ByteCode(108) // -

        val LDIV = ByteCode(109) // -

        val FDIV = ByteCode(110) // -

        val DDIV = ByteCode(111) // -

        val IREM = ByteCode(112) // -

        val LREM = ByteCode(113) // -

        val FREM = ByteCode(114) // -

        val DREM = ByteCode(115) // -

        val INEG = ByteCode(116) // -

        val LNEG = ByteCode(117) // -

        val FNEG = ByteCode(118) // -

        val DNEG = ByteCode(119) // -

        val ISHL = ByteCode(120) // -

        val LSHL = ByteCode(121) // -

        val ISHR = ByteCode(122) // -

        val LSHR = ByteCode(123) // -

        val IUSHR = ByteCode(124) // -

        val LUSHR = ByteCode(125) // -

        val IAND = ByteCode(126) // -

        val LAND = ByteCode(127) // -

        val IOR = ByteCode(128) // -

        val LOR = ByteCode(129) // -

        val IXOR = ByteCode(130) // -

        val LXOR = ByteCode(131) // -

        val IINC = ByteCode(132) // visitIincInsn

        val I2L = ByteCode(133) // visitInsn

        val I2F = ByteCode(134) // -

        val I2D = ByteCode(135) // -

        val L2I = ByteCode(136) // -

        val L2F = ByteCode(137) // -

        val L2D = ByteCode(138) // -

        val F2I = ByteCode(139) // -

        val F2L = ByteCode(140) // -

        val F2D = ByteCode(141) // -

        val D2I = ByteCode(142) // -

        val D2L = ByteCode(143) // -

        val D2F = ByteCode(144) // -

        val I2B = ByteCode(145) // -

        val I2C = ByteCode(146) // -

        val I2S = ByteCode(147) // -

        val LCMP = ByteCode(148) // -

        val FCMPL = ByteCode(149) // -

        val FCMPG = ByteCode(150) // -

        val DCMPL = ByteCode(151) // -

        val DCMPG = ByteCode(152) // -

        val IFEQ = ByteCode(153) // visitJumpInsn

        val IFNE = ByteCode(154) // -

        val IFLT = ByteCode(155) // -

        val IFGE = ByteCode(156) // -

        val IFGT = ByteCode(157) // -

        val IFLE = ByteCode(158) // -

        val IF_ICMPEQ = ByteCode(159) // -

        val IF_ICMPNE = ByteCode(160) // -

        val IF_ICMPLT = ByteCode(161) // -

        val IF_ICMPGE = ByteCode(162) // -

        val IF_ICMPGT = ByteCode(163) // -

        val IF_ICMPLE = ByteCode(164) // -

        val IF_ACMPEQ = ByteCode(165) // -

        val IF_ACMPNE = ByteCode(166) // -

        val GOTO = ByteCode(167) // -

        val JSR = ByteCode(168) // -

        val RET = ByteCode(169) // visitVarInsn

        val TABLESWITCH = ByteCode(170) // visiTableSwitchInsn

        val LOOKUPSWITCH = ByteCode(171) // visitLookupSwitch

        val IRETURN = ByteCode(172) // visitInsn

        val LRETURN = ByteCode(173) // -

        val FRETURN = ByteCode(174) // -

        val DRETURN = ByteCode(175) // -

        val ARETURN = ByteCode(176) // -

        val RETURN = ByteCode(177) // -

        val GETSTATIC = ByteCode(178) // visitFieldInsn

        val PUTSTATIC = ByteCode(179) // -

        val GETFIELD = ByteCode(180) // -

        val PUTFIELD = ByteCode(181) // -

        val INVOKEVIRTUAL = ByteCode(182) // visitMethodInsn

        val INVOKESPECIAL = ByteCode(183) // -

        val INVOKESTATIC = ByteCode(184) // -

        val INVOKEINTERFACE = ByteCode(185) // -

        val INVOKEDYNAMIC = ByteCode(186) // visitInvokeDynamicInsn

        val NEW = ByteCode(187) // visitTypeInsn

        val NEWARRAY = ByteCode(188) // visitIntInsn

        val ANEWARRAY = ByteCode(189) // visitTypeInsn

        val ARRAYLENGTH = ByteCode(190) // visitInsn

        val ATHROW = ByteCode(191) // -

        val CHECKCAST = ByteCode(192) // visitTypeInsn

        val INSTANCEOF = ByteCode(193) // -

        val MONITORENTER = ByteCode(194) // visitInsn

        val MONITOREXIT = ByteCode(195) // -

        val MULTIANEWARRAY = ByteCode(197) // visitMultiANewArrayInsn

        val IFNULL = ByteCode(198) // visitJumpInsn

        val IFNONNULL = ByteCode(199) // -
    }
}