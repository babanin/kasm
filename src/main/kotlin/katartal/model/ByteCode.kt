package katartal.model

@JvmInline
value class ByteCode(val opcode: UByte) {
    companion object {
        // The JVM opcode values (with the MethodVisitor method name used to visit them in comment, and
        // where '-' means 'same method name as on the previous line').
        // See https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html.
        val NOP = ByteCode(0u) // visitInsn

        val ACONST_NULL = ByteCode(1u) // -

        val ICONST_M1 = ByteCode(2u) // -

        val ICONST_0 = ByteCode(3u) // -

        val ICONST_1 = ByteCode(4u) // -

        val ICONST_2 = ByteCode(5u) // -

        val ICONST_3 = ByteCode(6u) // -

        val ICONST_4 = ByteCode(7u) // -

        val ICONST_5 = ByteCode(8u) // -

        val LCONST_0 = ByteCode(9u) // -

        val LCONST_1 = ByteCode(10u) // -

        val FCONST_0 = ByteCode(11u) // -

        val FCONST_1 = ByteCode(12u) // -

        val FCONST_2 = ByteCode(13u) // -

        val DCONST_0 = ByteCode(14u) // -

        val DCONST_1 = ByteCode(15u) // -

        val BIPUSH = ByteCode(16u) // visitIntInsn

        val SIPUSH = ByteCode(17u) // -

        val LDC = ByteCode(18u) // visitLdcInsn

        val ILOAD = ByteCode(21u) // visitVarInsn

        val LLOAD = ByteCode(22u) // -

        val FLOAD = ByteCode(23u) // -

        val DLOAD = ByteCode(24u) // -

        val ALOAD = ByteCode(25u) // -

        val ALOAD_0 = ByteCode(0x2Au) 
        
        val IALOAD = ByteCode(46u) // visitInsn

        val LALOAD = ByteCode(47u) // -

        val FALOAD = ByteCode(48u) // -

        val DALOAD = ByteCode(49u) // -

        val AALOAD = ByteCode(50u) // -

        val BALOAD = ByteCode(51u) // -

        val CALOAD = ByteCode(52u) // -

        val SALOAD = ByteCode(53u) // -

        val ISTORE = ByteCode(54u) // visitVarInsn

        val LSTORE = ByteCode(55u) // -

        val FSTORE = ByteCode(56u) // -

        val DSTORE = ByteCode(57u) // -

        val ASTORE = ByteCode(58u) // -

        val IASTORE = ByteCode(79u) // visitInsn

        val LASTORE = ByteCode(80u) // -

        val FASTORE = ByteCode(81u) // -

        val DASTORE = ByteCode(82u) // -

        val AASTORE = ByteCode(83u) // -

        val BASTORE = ByteCode(84u) // -

        val CASTORE = ByteCode(85u) // -

        val SASTORE = ByteCode(86u) // -

        val POP = ByteCode(87u) // -

        val POP2 = ByteCode(88u) // -

        val DUP = ByteCode(89u) // -

        val DUP_X1 = ByteCode(90u) // -

        val DUP_X2 = ByteCode(91u) // -

        val DUP2 = ByteCode(92u) // -

        val DUP2_X1 = ByteCode(93u) // -

        val DUP2_X2 = ByteCode(94u) // -

        val SWAP = ByteCode(95u) // -

        val IADD = ByteCode(96u) // -

        val LADD = ByteCode(97u) // -

        val FADD = ByteCode(98u) // -

        val DADD = ByteCode(99u) // -

        val ISUB = ByteCode(100u) // -

        val LSUB = ByteCode(101u) // -

        val FSUB = ByteCode(102u) // -

        val DSUB = ByteCode(103u) // -

        val IMUL = ByteCode(104u) // -

        val LMUL = ByteCode(105u) // -

        val FMUL = ByteCode(106u) // -

        val DMUL = ByteCode(107u) // -

        val IDIV = ByteCode(108u) // -

        val LDIV = ByteCode(109u) // -

        val FDIV = ByteCode(110u) // -

        val DDIV = ByteCode(111u) // -

        val IREM = ByteCode(112u) // -

        val LREM = ByteCode(113u) // -

        val FREM = ByteCode(114u) // -

        val DREM = ByteCode(115u) // -

        val INEG = ByteCode(116u) // -

        val LNEG = ByteCode(117u) // -

        val FNEG = ByteCode(118u) // -

        val DNEG = ByteCode(119u) // -

        val ISHL = ByteCode(120u) // -

        val LSHL = ByteCode(121u) // -

        val ISHR = ByteCode(122u) // -

        val LSHR = ByteCode(123u) // -

        val IUSHR = ByteCode(124u) // -

        val LUSHR = ByteCode(125u) // -

        val IAND = ByteCode(126u) // -

        val LAND = ByteCode(127u) // -

        val IOR = ByteCode(128u) // -

        val LOR = ByteCode(129u) // -

        val IXOR = ByteCode(130u) // -

        val LXOR = ByteCode(131u) // -

        val IINC = ByteCode(132u) // visitIincInsn

        val I2L = ByteCode(133u) // visitInsn

        val I2F = ByteCode(134u) // -

        val I2D = ByteCode(135u) // -

        val L2I = ByteCode(136u) // -

        val L2F = ByteCode(137u) // -

        val L2D = ByteCode(138u) // -

        val F2I = ByteCode(139u) // -

        val F2L = ByteCode(140u) // -

        val F2D = ByteCode(141u) // -

        val D2I = ByteCode(142u) // -

        val D2L = ByteCode(143u) // -

        val D2F = ByteCode(144u) // -

        val I2B = ByteCode(145u) // -

        val I2C = ByteCode(146u) // -

        val I2S = ByteCode(147u) // -

        val LCMP = ByteCode(148u) // -

        val FCMPL = ByteCode(149u) // -

        val FCMPG = ByteCode(150u) // -

        val DCMPL = ByteCode(151u) // -

        val DCMPG = ByteCode(152u) // -

        val IFEQ = ByteCode(153u) // visitJumpInsn

        val IFNE = ByteCode(154u) // -

        val IFLT = ByteCode(155u) // -

        val IFGE = ByteCode(156u) // -

        val IFGT = ByteCode(157u) // -

        val IFLE = ByteCode(158u) // -

        val IF_ICMPEQ = ByteCode(159u) // -

        val IF_ICMPNE = ByteCode(160u) // -

        val IF_ICMPLT = ByteCode(161u) // -

        val IF_ICMPGE = ByteCode(162u) // -

        val IF_ICMPGT = ByteCode(163u) // -

        val IF_ICMPLE = ByteCode(164u) // -

        val IF_ACMPEQ = ByteCode(165u) // -

        val IF_ACMPNE = ByteCode(166u) // -

        val GOTO = ByteCode(167u) // -

        val JSR = ByteCode(168u) // -

        val RET = ByteCode(169u) // visitVarInsn

        val TABLESWITCH = ByteCode(170u) // visiTableSwitchInsn

        val LOOKUPSWITCH = ByteCode(171u) // visitLookupSwitch

        val IRETURN = ByteCode(172u) // visitInsn

        val LRETURN = ByteCode(173u) // -

        val FRETURN = ByteCode(174u) // -

        val DRETURN = ByteCode(175u) // -

        val ARETURN = ByteCode(176u) // -

        val RETURN = ByteCode(177u) // -

        val GETSTATIC = ByteCode(178u) // visitFieldInsn

        val PUTSTATIC = ByteCode(179u) // -

        val GETFIELD = ByteCode(180u) // -

        val PUTFIELD = ByteCode(181u) // -

        val INVOKEVIRTUAL = ByteCode(182u) // visitMethodInsn

        val INVOKESPECIAL = ByteCode(183u) // -

        val INVOKESTATIC = ByteCode(184u) // -

        val INVOKEINTERFACE = ByteCode(185u) // -

        val INVOKEDYNAMIC = ByteCode(186u) // visitInvokeDynamicInsn

        val NEW = ByteCode(187u) // visitTypeInsn

        val NEWARRAY = ByteCode(188u) // visitIntInsn

        val ANEWARRAY = ByteCode(189u) // visitTypeInsn

        val ARRAYLENGTH = ByteCode(190u) // visitInsn

        val ATHROW = ByteCode(191u) // -

        val CHECKCAST = ByteCode(192u) // visitTypeInsn

        val INSTANCEOF = ByteCode(193u) // -

        val MONITORENTER = ByteCode(194u) // visitInsn

        val MONITOREXIT = ByteCode(195u) // -

        val MULTIANEWARRAY = ByteCode(197u) // visitMultiANewArrayInsn

        val IFNULL = ByteCode(198u) // visitJumpInsn

        val IFNONNULL = ByteCode(199u) // -
    }
}