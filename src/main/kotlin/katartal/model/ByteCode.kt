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
enum class ByteCode(
    val opcode: UByte,
    
    val stackChange: Int = 0,
    val resetStack: Int? = null,
    
    val expectedParameters: Int = 0
) {
    /**
     * Perform no operation
     */
    NOP(0x0u), // 0

    /**
     * Push a null reference onto the stack
     * → null
     */
    ACONST_NULL(0x1u), // 1

    /**
     * Load the int value -1 onto the stack
     * → -1
     */
    ICONST_M1(0x2u),

    /**
     * Load the int value 0 onto the stack
     * → 0
     */
    ICONST_0(0x3u, stackChange = 1),

    /**
     * Load the int value 1 onto the stack
     * → 1
     */
    ICONST_1(0x4u, stackChange = 1),

    /**
     * Load the int value 2 onto the stack
     * → 2
     */
    ICONST_2(0x5u, stackChange = 1),

    /**
     * Load the int value 3 onto the stack
     * → 3
     */
    ICONST_3(0x6u, stackChange = 1),

    /**
     * Load the int value 4 onto the stack
     * → 4
     */
    ICONST_4(0x7u, stackChange = 1),

    /**
     * Load the int value 5 onto the stack
     * → 5
     */
    ICONST_5(0x8u, stackChange = 1),

    /**
     * Push 0L onto the stack
     * → 0L
     */
    LCONST_0(0x9u, stackChange = 1),

    /**
     * Push 1L onto the stack
     * → 1L
     */
    LCONST_1(0xAu, stackChange = 1),

    /**
     * Push 0.0f on the stack
     * → 0.0f
     */
    FCONST_0(0xBu, stackChange = 1),

    /**
     * Push 0.0f on the stack
     * → 1.0f
     */
    FCONST_1(0xCu, stackChange = 1),

    /**
     * Push 0.0f on the stack
     * → 2.0f
     */
    FCONST_2(0xDu, stackChange = 1),

    /**
     * Push the constant 0.0 (a double) onto the stack
     * → 0.0
     */
    DCONST_0(0xEu, stackChange = 1),

    /**
     * Push the constant 1.0 (a double) onto the stack
     * → 1.0
     */
    DCONST_1(0xFu, stackChange = 1),

    /**
     * Push a byte onto the stack as an integer value
     * → value
     * @param byte
     */
    BIPUSH(0x10u),

    /**
     * Push a short onto the stack as an integer value
     * → value
     * @param byte1
     * @param byte2
     */
    SIPUSH(0x11u, stackChange = 1, expectedParameters = 2),

    /**
     * Push a constant #index from a constant pool (
     * String, int, float, Class, java.lang.invoke.MethodType,
     * java.lang.invoke.MethodHandle, or a dynamically-computed constant) onto the stack
     * → value
     * @param index
     */
    LDC(0x12u, stackChange = 1, expectedParameters = 1), // 18
    
    /**
     * Push a constant #index from a constant pool (
     * String, int, float, Class, java.lang.invoke.MethodType,
     * java.lang.invoke.MethodHandle, or a dynamically-computed constant) onto the stack
     * → value
     * @param indexbyte1
     * @param indexbyte2
     */
    LDC_W(0x13u, stackChange = 1, expectedParameters = 2), // 19

    /**
     * Push a constant #index from a constant pool (double, long, or a dynamically-computed constant) onto the stack
     * → value
     * @param indexbyte1
     * @param indexbyte2
     */
    LDC2_W(0x14u), // 20

    /**
     * load an int value from a local variable #index
     * → value
     * @param index
     */
    ILOAD(0x15u, stackChange = 1, expectedParameters = 1), // 21

    /**
     * Load a long value from a local variable #index
     * → value
     * @param index
     */
    LLOAD(0x16u, stackChange = 1, expectedParameters = 1), // 22

    /**
     * Load a float value from a local variable #index
     * → value
     * @param index
     */
    FLOAD(0x17u, stackChange = 1, expectedParameters = 1), // 23

    /**
     * Load a double value from a local variable #index
     * → value
     * @param index
     */
    DLOAD(0x18u, stackChange = 1, expectedParameters = 1), // 24

    /**
     * Load a reference onto the stack from a local variable #index
     * → objectref
     * @param index
     */
    ALOAD(0x19u, stackChange = 1, expectedParameters = 1), // 25

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
    ILOAD_3(0x1Du, stackChange = 1), // 29
    
    /**
     * Load a long value from local variable 0
     * → value
     */
    LLOAD_0(0x1Eu, stackChange = 1), // 30

    /**
     * Load a long value from local variable 1
     * → value
     */
    LLOAD_1(0x1Fu, stackChange = 1), // 31

    /**
     * Load a long value from local variable 2
     * → value
     */
    LLOAD_2(0x20u, stackChange = 1), // 32

    /**
     * Load a long value from local variable 3
     * → value
     */
    LLOAD_3(0x21u, stackChange = 1), // 33

    /**
     * Load a float value from local variable 0
     * → value
     */
    FLOAD_0(0x22u, stackChange = 1), // 34

    /**
     * Load a float value from local variable 1
     * → value
     */
    FLOAD_1(0x23u, stackChange = 1), // 35

    /**
     * Load a float value from local variable 2
     * → value
     */
    FLOAD_2(0x24u, stackChange = 1), // 36

    /**
     * Load a float value from local variable 3
     * → value
     */
    FLOAD_3(0x25u, stackChange = 1), // 37

    /**
     * Load a double value from local variable 0
     * → value
     */
    DLOAD_0(0x26u, stackChange = 1), // 38

    
    /**
     * Load a double value from local variable 0
     * → value
     */
    DLOAD_1(0x27u, stackChange = 1), // 39

    
    /**
     * Load a double value from local variable 0
     * → value
     */
    DLOAD_2(0x28u, stackChange = 1), // 40

    
    /**
     * Load a double value from local variable 0
     * → value
     */
    DLOAD_3(0x29u, stackChange = 1), // 41

    /**
     * Load a reference onto the stack from local variable 0
     * → objectref
     */
    ALOAD_0(0x2Au, stackChange = 1), // 42

    /**
     * Load a reference onto the stack from local variable 1
     * → objectref
     */
    ALOAD_1(0x2Bu, stackChange = 1, expectedParameters = 0), // 43

    /**
     * Load a reference onto the stack from local variable 2
     * → objectref
     */
    ALOAD_2(0x2Cu, stackChange = 1), // 44

    /**
     * Load a reference onto the stack from local variable 3
     * → objectref
     */
    ALOAD_3(0x2Du, stackChange = 1), // 45

    /**
     * Load an int from an array
     * arrayref, index → value
     */
    IALOAD(0x2Eu, stackChange = -1), // 46

    /**
     * Load a long from an array
     * arrayref, index → value
     */
    LALOAD(0x2Fu, stackChange = -1), // 47

    /**
     * Load a float from an array
     * arrayref, index → value
     */
    FALOAD(0x30u, stackChange = -1), // 48

    /**
     * Load a double from an array
     * arrayref, index → value
     */
    DALOAD(0x31u, stackChange = -1), // 49

    /**
     * Load onto the stack a reference from an array
     * arrayref, index → value
     */
    AALOAD(0x32u, stackChange = -1), // 50

    /**
     * Load a byte or Boolean value from an array
     * arrayref, index → value
     */
    BALOAD(0x33u, stackChange = -1), // 51

    /**
     * Load a char from an array
     * arrayref, index → value
     */
    CALOAD(0x34u, stackChange = -1), // 52

    /**
     * Load short from an array
     * arrayref, index → value
     */
    SALOAD(0x35u, stackChange = -1), // 53

    /**
     * Store int value into variable #index
     * value →
     * @param index
     */
    ISTORE(0x36u, stackChange = -1, expectedParameters = 1), // 54

    /**
     * Store a long value in a local variable #index
     * value →
     * @param index
     */
    LSTORE(0x37u, stackChange = -1, expectedParameters = 1), // 55

    /**
     * Store a float value into a local variable #index
     * value →
     * @param index
     */
    FSTORE(0x38u), // 56

    /**
     * Store a float value into a local variable #index
     * value →
     * @param index
     */
    DSTORE(0x39u), // 57

    /**
     * Store a float value into a local variable #index
     * value →
     * @param index
     */
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

    /**
     * Duplicate the value on top of the stack
     * value → value, value
     */
    DUP(0x59u, stackChange = 1), // 89
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

    /**
     * int subtract
     * value1, value2 → result
     */
    ISUB(100u, stackChange = -1, expectedParameters = 0),
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

    /**
     * Logical int remainder
     * value1, value2 → result
     */
    IREM(0x70u, stackChange = -1, expectedParameters = 0), // 112

    /**
     * Logical long remainder
     * value1, value2 → result
     */
    LREM(0x71u, stackChange = -1, expectedParameters = 0), // 113
    FREM(0x72u), // 114
    DREM(0x73u), // 115
    INEG(0x74u), // 116
    LNEG(117u), // 117
    FNEG(118u), // 118
    DNEG(119u), // 119
    ISHL(120u), // 120
    LSHL(121u), // 121
    ISHR(122u), // 122
    LSHR(123u), // 123
    IUSHR(124u), // 124
    LUSHR(125u), // 125
    IAND(126u), // 126
    LAND(127u), // 127
    IOR(128u), // 128
    LOR(129u), // 129
    IXOR(130u), // 130
    LXOR(131u), // 131

    /**
     * Increment local variable #index by signed byte const
     * [No change]
     * @param index
     * @param const
     */
    IINC(132u, expectedParameters = 2), // 132
    I2L(133u), // 133
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

    /**
     * if value is 0, branch to instruction at branchoffset
     * value →
     * @param branchbyte1
     * @param branchbyte2
     */
    IFEQ(0x99u, stackChange = -1, expectedParameters = 2), // 153

    /**
     * If value is not 0, branch to instruction at branchoffset
     * value →
     * @param branchbyte1
     * @param branchbyte2
     */
    IFNE(0x9Au, stackChange = -1, expectedParameters = 2), // 154
    
    IFLT(155u), // 155
    IFGE(156u), // 156
    IFGT(157u), // 157
    IFLE(158u), // 158
    IF_ICMPEQ(159u),
    IF_ICMPNE(160u),
    IF_ICMPLT(161u),
    IF_ICMPGE(162u),

    /**
     * If value1 is greater than value2, branch to instruction at branchoffset
     * value1, value2 →
     * @param branchbyte1
     * @param branchbyte2
     */
    IF_ICMPGT(163u, stackChange = -2, expectedParameters = 2),
    IF_ICMPLE(164u),
    IF_ACMPEQ(165u),
    IF_ACMPNE(166u),

    /**
     * Goes to another instruction at branchoffset
     * →
     * @param branchbyte1
     * @param branchbyte2
     */
    GOTO(0xA7u, stackChange = 0, expectedParameters = 2),
    
    JSR(168u),
    RET(169u),
    TABLESWITCH(170u),
    LOOKUPSWITCH(171u),

    /**
     * Return an integer from a method
     * value → [empty]
     */
    IRETURN(0xACu, resetStack = 0), // 172

    /**
     * Return a long from a method
     * value → [empty]
     */
    LRETURN(0xADu), // 173

    /**
     * Return a floa from a method
     * value → [empty]
     */
    FRETURN(0xAEu), // 174

    /**
     * Return a double from a method
     * value → [empty]
     */
    DRETURN(0xAFu), // 175

    /**
     * Return a reference from a method
     * value → [empty]
     */
    ARETURN(0xB0u, resetStack = 0), // 176

    /**
     * Return void from method
     * → [empty]
     */
    RETURN(0xB1u), // 177

    /**
     * Get a static field value of a class, where the field is identified by field reference in the constant pool index
     * @param indexbyte1
     * @param indexbyte2
     */
    GETSTATIC(178u, stackChange = 1, expectedParameters = 2),

    /**
     * Set static field to value in a class, where the field is identified by a field reference index in constant pool
     * value →
     * @param indexbyte1
     * @param indexbyte2
     */
    PUTSTATIC(0xB3u, stackChange = -1, expectedParameters = 2), // 179

    /**
     * Get a field value of an object objectref, where the field is identified by field reference in the constant pool index
     * objectref → value
     * @param indexbyte1
     * @param indexbyte2
     */
    GETFIELD(0xB4u, stackChange = 0, expectedParameters = 2), // 180

    /**
     * Set field to value in an object objectref, where the field is identified by a field reference index in constant pool
     * objectref, value →
     * @param indexbyte1
     * @param indexbyte2
     */
    PUTFIELD(0xB5u, stackChange = -2, expectedParameters = 2), // 181
    INVOKEVIRTUAL(0xB6u), // 182

    /**
     * Invoke instance method on object objectref and puts the result on the stack (might be void).
     * The method is identified by method reference index in constant pool.
     * objectref, [arg1, arg2, ...] → result
     * @param indexbyte1
     * @param indexbyte2
     */
    INVOKESPECIAL(0xB7u, resetStack = 1,  expectedParameters = 2), // 183

    /**
     * Invoke a static method and puts the result on the stack (might be void); 
     * the method is identified by method reference index in constant pool
     * @param indexbyte1
     * @param indexbyte2
     */
    INVOKESTATIC(0xB8u, resetStack = 1, expectedParameters = 2), // 184
    INVOKEINTERFACE(0xB9u), // 185

    /**
     * Invokes a dynamic method and puts the result on the stack (might be void); 
     * the method is identified by method reference index in constant pool
     * @param indexbyte1
     * @param indexbyte2
     */
    INVOKEDYNAMIC(0xBAu, resetStack = 1), // 186

    /**
     * Create new object of type identified by class reference in constant pool index
     * → objectref
     * @param indexbyte1
     * @param indexbyte2
     */
    NEW(0xBBu, stackChange = 1, expectedParameters = 2), // 187

    /**
     * Create new array with count elements of primitive type identified by atype
     * count → arrayref
     * @param 1: atype
     */
    NEWARRAY(188u, stackChange = 0, expectedParameters = 1),

    /**
     * Create a new array of references of length count and component type identified by the class reference index
     * count → arrayref
     * @param indexbyte1
     * @param indexbyte2
     */
    ANEWARRAY(189u, stackChange = 0, expectedParameters = 2),
    ARRAYLENGTH(190u),
    ATHROW(191u),

    /**
     * Checks whether an objectref is of a certain type, the class reference of which is in the constant pool at index
     * objectref → objectref
     * @param indexbyte1
     * @param indexbyte2
     */
    CHECKCAST(0xC0u), // 192
    INSTANCEOF(193u),
    MONITORENTER(194u),
    MONITOREXIT(195u),
    MULTIANEWARRAY(197u),
    IFNULL(198u),
    IFNONNULL(199u)
}