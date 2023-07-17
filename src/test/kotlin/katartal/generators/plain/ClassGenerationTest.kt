package katartal.generators.plain

import katartal.dsl._class
import katartal.model.ByteCode.*
import katartal.model.method.CodeBuilder
import katartal.model.method.StackFrameBuilder.IntegerVar
import katartal.model.method.StackFrameBuilder.ObjectVar
import katartal.model.method.MethodAccess.Companion.PUBLIC
import katartal.model.method.MethodAccess.Companion.STATIC
import katartal.util.ByteArrayClassLoader
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import util.assertThat
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
        val toClass = classLoader.loadClass(klass.name, clsBytes)

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
        val toClass = classLoader.loadClass(klass.name, clsBytes)

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
        val toClass = classLoader.loadClass(klass.name, clsBytes)

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
        val toClass = classLoader.loadClass(klass.name, clsBytes)

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
                    _return(true)
                }
            } returns Boolean::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.name, clsBytes)

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
                    _return(true)
                }
            } returns Boolean::class.java throws FileNotFoundException::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass).isNotNull
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
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass).isNotNull
    }


    @Test
    fun shouldGenerateFizzBuzz() {
        // given
        val klass = _class("Test") {
            _method("fizzBuzz", listOf("count" to Int::class.java), PUBLIC + STATIC) {
                _code(maxLocals = 3, maxStack = 3) {
                    // String[] result = new String[count]
                    _instruction(ILOAD_0)
                    _instruction(ANEWARRAY) {
                        _referenceU2(constantPool.writeClass("java/lang/String"))
                    }
                    _instruction(ASTORE_1)

                    _instruction(ILOAD_2) // i
                    _instruction(ILOAD_0) // count

                    // if(i % 3 == 0 & i % 5 == 0)
                    _if(IF_ICMPGT) {
                        // i % 3 
                        _mathOperation(IREM, ILOAD_2, ICONST_5)
                        _instruction(IFNE) {
                            _referenceU2(34u) // 
                        }
                        // i % 3 
                        _mathOperation(IREM, ILOAD_2, ICONST_5)
                        _instruction(IFNE) {
                            _referenceU2(34u) // 
                        }

                        // then
                        _instruction(ALOAD_1)
                        _mathOperation(ISUB, ILOAD_2, ICONST_1)
                        _ldc("FizzBuzz")
                        _instruction(AASTORE)
                    }

                    // else if(i % 3) 
                    _mathOperation(IREM, ILOAD_2, ICONST_3)
                    _if(IFNE) {
                        _instruction(IFNE) {
                            _referenceU2(50u) // 
                        }

                        _instruction(ALOAD_1)
                        _mathOperation(ISUB, ILOAD_2, ICONST_1)
                        _ldc("Fizz")
                        _instruction(AASTORE)
                    }

                    _instruction(ALOAD_1)
                    _instruction(ARETURN)
                }
            } returns Array<String>::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass)
            .isNotNull
            .hasMethods("fizzBuzz")

        val fizzBuzzMethod = toClass.getDeclaredMethod("fizzBuzz", Int::class.java)
        val result: Array<String> = fizzBuzzMethod.invoke(null, 15) as Array<String>
        Assertions.assertThat(result)
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
                _locals {
                    _var("result", IntArray::class.java, 4u, 19u)
                    _var("i", Int::class.java, 6u, 15u)
                }

                _code(maxLocals = 2, maxStack = 3) {
                    // Locals:
                    //   0: count (parameter)
                    //   1: int[] array (result)
                    //   2: i (cycle variable)

                    // String[] result = new String[count]
                    _instruction(ILOAD_0)
                    _primitiveArray(CodeBuilder.PrimitiveArrayType.T_INT)
                    _instruction(ASTORE_1)

                    _instruction(ICONST_0)
                    _instruction(ISTORE_2)

                    _stackFrame { 
//                        _append(IntegerVar())
                    }

                    val forLabel = label()
                    _instruction(ILOAD_2) // i
                    _instruction(ILOAD_0) // count

                    _if(IF_ICMPGE) {
                        _instruction(ALOAD_1)
                        _instruction(ILOAD_2)
                        _instruction(ILOAD_2)
                        _instruction(IASTORE)
                        _instruction(IINC) {
                            _index(2u)
                            _const(1)
                        }
                        
                        _goto(forLabel)
                    }
                    
//                    _stackFrame {
//                        _full(locals = listOf(ObjectVar(IntArray::class.java)))
//                    }
                    
                    _stackFrame { 
                        _same()
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
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass)
            .isNotNull
            .hasMethods("firstNIntegers")

        val fizzBuzzMethod = toClass.getDeclaredMethod("firstNIntegers", Int::class.java)
        val result: IntArray = fizzBuzzMethod.invoke(null, 15) as IntArray
        Assertions.assertThat(result)
            .containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
    }

    fun print(array: ByteArray) {
        val fop = FileOutputStream(File("Test.class"))
        fop.write(array)
        fop.close()
    }
}