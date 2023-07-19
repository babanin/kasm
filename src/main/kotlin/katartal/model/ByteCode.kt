package katartal.model

/**
 * See https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html
 * https://en.wikipedia.org/wiki/List_of_Java_bytecode_instructions
 *
 * Format:
 *      [count]: [operand labels]
 *      <description>
 *      <stack before> → <stack after>
 */
enum class ByteCode(val opcode: UByte, val stackChange: Int = 0) {
    NOP(0u),
    ACONST_NULL(1u),

    /**
     * Load the int value -1 onto the stack
     * → -1
     */
    ICONST_M1(2u),

    /**
     * Load the int value 0 onto the stack
     * → 0
     */
    ICONST_0(3u, stackChange = 1),

    /**
     * Load the int value 1 onto the stack
     * → 1
     */
    ICONST_1(4u, stackChange = 1),

    /**
     * Load the int value 2 onto the stack
     * → 2
     */
    ICONST_2(5u, stackChange = 1),

    /**
     * Load the int value 3 onto the stack
     * → 3
     */
    ICONST_3(6u, stackChange = 1),

    /**
     * Load the int value 4 onto the stack
     * → 4
     */
    ICONST_4(7u, stackChange = 1),

    /**
     * Load the int value 5 onto the stack
     * → 5
     */
    ICONST_5(8u, stackChange = 1),

    LCONST_0(9u, stackChange = 1),
    LCONST_1(10u, stackChange = 1),
    FCONST_0(11u, stackChange = 1),
    FCONST_1(12u, stackChange = 1),
    FCONST_2(13u, stackChange = 1),
    DCONST_0(14u, stackChange = 1),
    DCONST_1(15u, stackChange = 1),
    BIPUSH(16u),

    /**
     * Push a short onto the stack as an integer value
     * → value
     * @param byte1
     * @param byte2
     */
    SIPUSH(17u, stackChange = 1),
    LDC(18u),
    LDC_W(19u),
    LDC2_W(20u),
    ILOAD(0x15u),
    LLOAD(22u),
    FLOAD(23u),
    DLOAD(24u),
    ALOAD(25u),

    /**
     * Load an int value from local variable 0
     * → value
     */
    ILOAD_0(0x1Au, stackChange = 1), // 26

    /**
     * Load an int value from local variable 1
     * → value
     */
    ILOAD_1(0x1Bu, stackChange = 1), // 27

    /**
     * Load an int value from local variable 2
     * → value
     */
    ILOAD_2(0x1Cu, stackChange = 1), // 28

    /**
     * Load an int value from local variable 3
     * → value
     */
    ILOAD_3(0x1Eu, stackChange = 1), // 29

    /**
     * Load a reference onto the stack from local variable 0
     * → objectref
     */
    ALOAD_0(0x2Au, stackChange = 1), // 30

    /**
     * Load a reference onto the stack from local variable 1
     * → objectref
     */
    ALOAD_1(0x2Bu, stackChange = 1), // 31

    /**
     * Load a reference onto the stack from local variable 2
     * → objectref
     */
    ALOAD_2(0x2Cu, stackChange = 1), // 32

    /**
     * Load a reference onto the stack from local variable 3
     * → objectref
     */
    ALOAD_3(0x2Du, stackChange = 1), // 33

    IALOAD(46u),
    LALOAD(47u),
    FALOAD(48u),
    DALOAD(49u),
    AALOAD(50u),
    BALOAD(51u),
    CALOAD(52u),
    SALOAD(53u),
    ISTORE(54u),

    /**
     * Store a long value in a local variable #index
     * value →
     * @param index
     */
    LSTORE(55u),
    FSTORE(56u),
    DSTORE(57u),
    ASTORE(0x3Au), // 58

    /**
     * Store int value into variable 0
     * value →
     */
    ISTORE_0(0x3Bu, stackChange = -1), // 59

    /**
     * Store int value into variable 1
     * value →
     */
    ISTORE_1(0x3Cu, stackChange = -1), // 60

    /**
     * Store int value into variable 2
     * value →
     */
    ISTORE_2(0x3Du, stackChange = -1), // 61

    /**
     * Store int value into variable 3
     * value →
     */
    ISTORE_3(0x3Eu, stackChange = -1), // 62

    /**
     * Store a reference into local variable 0
     * objectref →
     */
    ASTORE_0(0x4Bu, stackChange = -1), // 75

    /**
     * Store a reference into local variable 1
     * objectref →
     */
    ASTORE_1(0x4Cu, stackChange = -1), // 76

    /**
     * Store a reference into local variable 2
     * objectref →
     */
    ASTORE_2(0x4Du, stackChange = -1), // 77

    /**
     * Store a reference into local variable 3
     * objectref →
     */
    ASTORE_3(0x4Eu, stackChange = -1), // 78

    /**
     * Store an int into an array
     * arrayref, index, value →
     */
    IASTORE(79u, stackChange = -3),

    /**
     * [No parameters]
     * Store a long to an array
     * arrayref, index, value →
     */
    LASTORE(80u, stackChange = -3),

    /**
     * Store a float to an array
     * arrayref, index, value →
     */
    FASTORE(81u, stackChange = -3),

    /**
     * Store a double to an array
     * arrayref, index, value →
     */
    DASTORE(82u, stackChange = -3),

    /**
     * Store a reference to an array
     * arrayref, index, value →
     */
    AASTORE(83u, stackChange = -3),

    /**
     * Store a byte or Boolean value into an array
     * arrayref, index, value →
     */
    BASTORE(84u, stackChange = -3),

    /**
     * Store a char into an array
     * arrayref, index, value →
     */
    CASTORE(85u, stackChange = -3),

    /**
     * Store a short to an array
     * arrayref, index, value →
     */
    SASTORE(86u, stackChange = -3),

    POP(87u),
    POP2(88u),
    DUP(89u),
    DUP_X1(90u),
    DUP_X2(91u),
    DUP2(92u),
    DUP2_X1(93u),
    DUP2_X2(94u),
    SWAP(95u),
    IADD(96u),
    LADD(97u),
    FADD(98u),
    DADD(99u),
    ISUB(100u, stackChange = -1),
    LSUB(101u),
    FSUB(102u),
    DSUB(103u),
    IMUL(104u),
    LMUL(105u),
    FMUL(106u),
    DMUL(107u),
    IDIV(108u),
    LDIV(109u),
    FDIV(110u),
    DDIV(111u),
    IREM(112u, stackChange = -1),
    LREM(113u),
    FREM(114u),
    DREM(115u),
    INEG(116u),
    LNEG(117u),
    FNEG(118u),
    DNEG(119u),
    ISHL(120u),
    LSHL(121u),
    ISHR(122u),
    LSHR(123u),
    IUSHR(124u),
    LUSHR(125u),
    IAND(126u),
    LAND(127u),
    IOR(128u),
    LOR(129u),
    IXOR(130u),
    LXOR(131u),

    /**
     * Increment local variable #index by signed byte const
     * [No change]
     * @param index
     * @param const
     */
    IINC(132u),
    I2L(133u),
    I2F(134u),
    I2D(135u),
    L2I(136u),
    L2F(137u),
    L2D(138u),
    F2I(139u),
    F2L(140u),
    F2D(141u),
    D2I(142u),
    D2L(143u),
    D2F(144u),
    I2B(145u),
    I2C(146u),
    I2S(147u),
    LCMP(148u),
    FCMPL(149u),
    FCMPG(150u),
    DCMPL(151u),
    DCMPG(152u),
    IFEQ(153u),

    /**
     * If value is not 0, branch to instruction at branchoffset
     * value →
     * @param branchbyte1
     * @param branchbyte2
     */
    IFNE(154u),
    IFLT(155u),
    IFGE(156u),
    IFGT(157u),
    IFLE(158u),
    IF_ICMPEQ(159u),
    IF_ICMPNE(160u),
    IF_ICMPLT(161u),
    IF_ICMPGE(162u),
    IF_ICMPGT(163u),
    IF_ICMPLE(164u),
    IF_ACMPEQ(165u),
    IF_ACMPNE(166u),
    GOTO(167u),
    JSR(168u),
    RET(169u),
    TABLESWITCH(170u),
    LOOKUPSWITCH(171u),
    IRETURN(172u),
    LRETURN(173u),
    FRETURN(174u),
    DRETURN(175u),
    ARETURN(176u),
    RETURN(177u),
    GETSTATIC(178u),
    PUTSTATIC(179u),
    GETFIELD(180u),

    /**
     * Set field to value in an object objectref, where the field is identified by a field reference index in constant pool
     * objectref, value →
     * @param indexbyte1
     * @param indexbyte2
     */
    PUTFIELD(181u, stackChange = -2),
    INVOKEVIRTUAL(182u),

    /**
     * Invoke instance method on object objectref and puts the result on the stack (might be void).
     * The method is identified by method reference index in constant pool.
     * objectref, [arg1, arg2, ...] → result
     * @param indexbyte1
     * @param indexbyte2
     */
    INVOKESPECIAL(183u),
    INVOKESTATIC(184u),
    INVOKEINTERFACE(185u),
    INVOKEDYNAMIC(186u),
    NEW(187u),

    /**
     * Create new array with count elements of primitive type identified by atype
     * count → arrayref
     * @param 1: atype
     */
    NEWARRAY(188u),
    ANEWARRAY(189u),
    ARRAYLENGTH(190u),
    ATHROW(191u),
    CHECKCAST(192u),
    INSTANCEOF(193u),
    MONITORENTER(194u),
    MONITOREXIT(195u),
    MULTIANEWARRAY(197u),
    IFNULL(198u),
    IFNONNULL(199u)
}