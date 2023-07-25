package katartal.generators.plain

import katartal.dsl._class
import katartal.model.ByteCode.*
import katartal.model.method.CodeBuilder
import katartal.model.method.MethodAccess.Companion.PUBLIC
import katartal.model.method.MethodAccess.Companion.STATIC
import katartal.model.method.StackFrameBuilder.IntegerVar
import katartal.model.method.StackFrameBuilder.ObjectVar
import katartal.model.method.plugins.branching._if
import katartal.model.method.plugins.lvt.releaseVariable
import katartal.model.method.plugins.lvt.variable
import katartal.util.ByteArrayClassLoader
import katartal.util.path
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import util.Assertions.assertThat
import java.io.*

class ClassGenerationTest {
    @Test
    fun shouldGenerateEmptyValidClass() {
        // given
        val klass = _class("Test")

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
    }

    @Test
    fun shouldGenerateEmptyClassWhichExtendsCustomClass() {
        // given
        val klass = _class("Test") extends ArrayList::class.java

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .hasSuperclass(ArrayList::class.java)
    }

    @Test
    fun shouldGenerateValidClassWithImplementedMarkerInterface() {
        // given
        val klass = _class("Test") implements Serializable::class.java

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .implementsInterface(Serializable::class.java)
    }

    @Test
    fun shouldGenerateValidClassWithCustomConstructor() {
        // given
        val klass = _class("Test") {
            _constructor(listOf("name" to String::class.java)) {
                _code {
                    _invokeSpecial(Object::class.java, "<init>", "()V")
                    _return()
                }
            }
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .hasConstructor(String::class.java)
    }

    @Test
    fun shouldGenerateValidClassWithEqualsMethod() {
        // given
        val klass = _class("Test") {
            _method("equals", listOf("other" to Object::class.java)) {
                _code {
                    _loadIntOnStack(1)
                    _return(Boolean::class.java)
                }
            } returns Boolean::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .hasDeclaredMethods("equals")
    }

    @Test
    fun shouldGenerateValidClassEqualsMethodThrowsException() {
        // given
        val klass = _class("Test") {
            _method("equals", listOf("other" to Object::class.java)) {
                _code {
                    _loadIntOnStack(1)
                    _return("Z")
                }
            } returns Boolean::class.java throws FileNotFoundException::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull

        val equalsMethod = toClass.getDeclaredMethod("equals", Object::class.java)
        assertThat(equalsMethod.exceptionTypes)
            .contains(FileNotFoundException::class.java);
    }

    @Test
    fun shouldPrintHelloWorld() {
        // given
        val klass = _class("Test") {
            _method("main", listOf("args" to Array<String>::class.java), PUBLIC + STATIC) {
                _code {
                    _getstatic(System::class.java, "out", PrintStream::class.java)
                    _ldc("Hello world!")
                    _invokeVirtual(PrintStream::class.java, "println", "(Ljava/lang/String;)V")
                    _return()
                }
            }
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass).isNotNull
    }

    @Test
    fun shouldGenerateFizzBuzz() {
        // given
        val klass = _class("Test") {
            _method("fizzBuzz", listOf("count" to Int::class.java), PUBLIC + STATIC) {
                _code {
                    // String[] result = new String[count]
                    _instruction(ILOAD_0)
                    _instruction(ANEWARRAY) {
                        _indexU2(constantPool.writeClass("java/lang/String"))
                    }

                    val result = variable("result", Array<String>::class.java)
                    _instruction(ASTORE_1)

                    // i = 1
                    _instruction(ICONST_1)
                    _instruction(ISTORE_2)

                    _stackFrame {
                        _append(ObjectVar(Array<String>::class.java), IntegerVar())
                    }

                    label("for")

                    val i = variable("i", "I")
                    _instruction(ILOAD_2) // i
                    _instruction(ILOAD_0) // count

                    // i < count
                    _if(IF_ICMPGT) {
                        // i % 3 
                        _mathOperation(IREM, ILOAD_2, ICONST_3)
                        _if(IFNE) {
                            // i % 5 
                            _mathOperation(IREM, ILOAD_2, ICONST_5)
                            _if(IFNE) {
                                // then
                                _instruction(ALOAD_1)
                                _mathOperation(ISUB, ILOAD_2, ICONST_1)
                                _ldc("FizzBuzz")
                                _instruction(AASTORE)

                                _goto("i++")
                            }
                        }

                        _stackFrame {
                            _same()
                        }

                        // else if(i % 3) 
                        label("i%3")
                        _mathOperation(IREM, ILOAD_2, ICONST_3)
                        _if(IFNE) {
                            _instruction(ALOAD_1)
                            _mathOperation(ISUB, ILOAD_2, ICONST_1)
                            _ldc("Fizz")
                            _instruction(AASTORE)

                            _goto("i++")
                        }

                        _stackFrame {
                            _same()
                        }

                        // else if(i % 5) 
                        _mathOperation(IREM, ILOAD_2, ICONST_5)
                        _if(IFNE) {
                            _instruction(ALOAD_1)
                            _mathOperation(ISUB, ILOAD_2, ICONST_1)
                            _ldc("Buzz")
                            _instruction(AASTORE)
                        }

                        _stackFrame {
                            _same()
                        }

                        // i++
                        label("i++")
                        _instruction(IINC) {
                            _index(2u)
                            _const(1)
                        }

                        _goto("for")
                    }

                    releaseVariable(i)
                    _stackFrame {
                        _chop(1)
                    }

                    _instruction(ALOAD_1)
                    _instruction(ARETURN)

                    releaseVariable(result)
                }
            } returns Array<String>::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .hasMethods("fizzBuzz")

        val fizzBuzzMethod = toClass.getDeclaredMethod("fizzBuzz", Int::class.java)
        val result: Array<String> = fizzBuzzMethod.invoke(null, 100) as Array<String>
        assertThat(result)
            .hasSize(100)
            .contains("Fizz", "Buzz", "FizzBuzz")
    }

    /**
     *     static int[] fizzBuzz(int count) {
     *         int[] result = new int[count];
     *
     *         for(int i = 0; i < count; i++) {
     *             result[i] = i;
     *         }
     *
     *         return result;
     *     }
     */
    @Test
    fun shouldGenerateArrayOfFirstNIntegers() {
        // given
        val klass = _class("Test") {
            _method("firstNIntegers", listOf("count" to Int::class.java), PUBLIC + STATIC) {
                _var("result", IntArray::class.java, 4u, 19u)
                _var("i", Int::class.java, 6u, 15u)

                _code {
                    // Locals:
                    //   0: count (parameter)
                    //   1: int[] array (result)
                    //   2: i (cycle variable)

                    // String[] result = new String[count]
                    _instruction(ILOAD_0)
                    _primitiveArray(CodeBuilder.PrimitiveArrayType.T_INT)
                    _instruction(ASTORE_1)

                    // i = 0
                    _instruction(ICONST_0)
                    _instruction(ISTORE_2)

                    _stackFrame {
                        _append(ObjectVar(IntArray::class.java), IntegerVar())
                    }

                    val forLabel = label("forLabel")

                    // i < count
                    _instruction(ILOAD_2) // i
                    _instruction(ILOAD_0) // count
                    _if(IF_ICMPGE) {
                        // result[i] = i
                        _instruction(ALOAD_1)
                        _instruction(ILOAD_2)
                        _instruction(ILOAD_2)
                        _instruction(IASTORE)

                        // i++
                        _instruction(IINC) {
                            _index(2u)
                            _const(1)
                        }

                        _goto("forLabel")
                    }

                    _stackFrame {
                        _chop(1)
                    }

                    _instruction(ALOAD_1)
                    _instruction(ARETURN)
                }
            } returns IntArray::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .hasMethods("firstNIntegers")

        val fizzBuzzMethod = toClass.getDeclaredMethod("firstNIntegers", Int::class.java)
        val result: IntArray = fizzBuzzMethod.invoke(null, 15) as IntArray
        assertThat(result)
            .containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
    }

    /**
     *     static int[] fizzBuzz(int count) {
     *         int[] result = new int[count];
     *
     *         for(int i = 0; i < count; i++) {
     *             result[i] = i;
     *         }
     *
     *         return result;
     *     }
     */
    @Test
    fun shouldVerifyOdd() {
        // given
        val klass = _class("Test") {
            _method("isOdd", listOf("num" to Int::class.java), PUBLIC + STATIC) {
                _code(maxLocals = 0, maxStack = 2) {
                    // Locals:
                    //   0: num (parameter)

                    _mathOperation(IREM, ILOAD_0, ICONST_2)

                    _if(IFNE) {
                        _instruction(ICONST_1) // i
                        _instruction(IRETURN) // i
                    }

                    _stackFrame {
                        _same()
                    }

                    _instruction(ICONST_0)
                    _instruction(IRETURN)
                }
            } returns Boolean::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .hasMethods("isOdd")

        val fizzBuzzMethod = toClass.getDeclaredMethod("isOdd", Int::class.java)
        val result = fizzBuzzMethod.invoke(null, 15) as Boolean
        assertThat(result)
            .isFalse
    }


    /**
     *   public int divide(Integer a, Integer b) {
     *       try {
     *           return a / b;
     *       } catch (ArithmeticException e) {
     *           return 0;
     *       } catch (NullPointerException e) {
     *           return 1;
     *       }
     *   }
     */
    @Test
    fun shouldHandleDivisionExceptions() {
        // given
        val klass = _class("Test") {
            _method("divide", listOf("a" to Integer::class.java, "b" to Integer::class.java), PUBLIC + STATIC) {
                _code(maxLocals = 0, maxStack = 2) {

                    _exception {
                        _try {
                            _instruction(ALOAD_0)
                            _instruction(INVOKEVIRTUAL) {
                                _indexU2(constantPool.writeMethodRef(Integer::class.java.path(), "intValue", "()I"))

                            }

                            _instruction(ALOAD_1)
                            _instruction(INVOKEVIRTUAL) {
                                _indexU2(constantPool.writeMethodRef(Integer::class.java.path(), "intValue", "()I"))
                            }

                            _instruction(IDIV)
                            _return(Int::class.java)
                        }

                        _catch(ArithmeticException::class.java) {
                            _stackFrame {
                                _same_locals_1_stack_item(ObjectVar(ArithmeticException::class.java))
                            }
                            _instruction(ASTORE_2)
                            _loadIntOnStack(0)
                            _return(Int::class.java)
                        }

                        _catch("java/lang/NullPointerException") {
                            _stackFrame {
                                _same_locals_1_stack_item(ObjectVar(NullPointerException::class.java))
                            }
                            _instruction(ASTORE_2)
                            _loadIntOnStack(1)
                            _return(Int::class.java)
                        }
                    }

                }
            } returns Int::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .hasMethods("divide")

        val divideMethod = toClass.getDeclaredMethod("divide", Integer::class.java, Integer::class.java)

        Assertions.assertThat(divideMethod.invoke(null, 2, 1) as Int).isEqualTo(2)
        Assertions.assertThat(divideMethod.invoke(null, 2, null) as Int).isEqualTo(1)
        Assertions.assertThat(divideMethod.invoke(null, 2, 0) as Int).isEqualTo(0)
    }


    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
    annotation class ClassLevelAnnotation

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
    annotation class MethodLevelAnnotation

    @Target(AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
    annotation class FieldLevelAnnotation

    @Test
    fun shouldGenerateClassWithAnnotations() {
        // given
        val klass = _class("Counter") {
            _field("count", Int::class.java) {
                _annotate(FieldLevelAnnotation::class.java)
            }

            _method("isOdd") {
                _annotate(MethodLevelAnnotation::class.java)
            }

            _annotate(ClassLevelAnnotation::class.java)
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
    }

    fun print(array: ByteArray) {
        val fop = FileOutputStream(File("Test.class"))
        fop.write(array)
        fop.close()
    }
}