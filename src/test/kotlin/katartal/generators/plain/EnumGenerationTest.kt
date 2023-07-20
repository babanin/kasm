package katartal.generators.plain

import katartal.dsl._enum
import katartal.model.ByteCode
import katartal.model.ByteCode.*
import katartal.model.field.FieldAccess
import katartal.util.ByteArrayClassLoader
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import util.assertThat
import java.io.File
import java.io.FileOutputStream

class EnumGenerationTest {
    /**
     * public enum EmptyEnum {
     * }
     */
    @Test
    fun shouldGenerateEmptyValidEnum() {
        // given
        val klass = _enum("EmptyEnum")

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .isEnum()
    }

    fun print(array: ByteArray) {
        val fop = FileOutputStream(File("Test.class"))
        fop.write(array)
        fop.close()
    }

    /**
     * public enum EnumWithPlainValues {
     *     A, B, C, D;
     * }
     */
    @Test
    fun shouldGenerateEnumWithPlainValues() {
        // given
        val klass = _enum("EnumWithPlainValues") {
            _value("A")
            _value("B")
            _value("C")
            _value("D")
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .isEnum()
            .hasDeclaredFields("A", "B", "C", "D")
    }

    /**
     * public enum EnumWithFields {
     *     A(1000), B(10000), C(100000);
     *
     *     private final int num;
     *
     *     EWithInstanceFields(int num) {
     *         this.num = num;
     *     }
     *
     *     public int getNum() {
     *         return num;
     *     }
     * }
     */
    @Test
    fun shouldGenerateEnumWithFields() {
        // given
        val klass = _enum("EnumWithFields") {
            _value("A") {
                _loadIntOnStack(1000)
            }

            _value("B") {
                _loadIntOnStack(10000)
            }

            _value("C") {
                _loadIntOnStack(100000)
            }

            _field("num", Int::class.java, FieldAccess.PRIVATE)

            _method("getNum") {
                _code {
                    _instruction(ALOAD_0)
                    _instruction(GETFIELD) {
                        _indexU2(constantPool.writeFieldRef(className, "num", "I"))
                    }
                    _instruction(IRETURN)
                }
            } returns Int::class.java

            _constructor(listOf("name" to String::class.java, "ordinal" to Int::class.java, "num" to Int::class.java)) {
                _code {
                    _instruction(ALOAD_0)
                    _instruction(ALOAD_1)
                    _instruction(ILOAD_2)

                    _instruction(INVOKESPECIAL) {
                        _indexU2(constantPool.writeMethodRef("java/lang/Enum", "<init>", "(Ljava/lang/String;I)V"))
                    }

                    _instruction(ALOAD_0)
                    _instruction(ILOAD_3)
                    _instruction(PUTFIELD) {
                        _indexU2(constantPool.writeFieldRef(className, "num", "I"))
                    }

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

        assertThat(toClass)
            .isNotNull
            .isEnum()
            .hasDeclaredFields("A", "B", "C")

        // Read nums values to verify correct initialization
        val nums = mutableListOf<Int>()
        val values = toClass.getDeclaredMethod("values").invoke(null) as Array<*>
        for (value in values) {
            if (value != null) {
                nums += value.javaClass.getMethod("getNum").invoke(value) as Int
            }
        }
        
        Assertions.assertThat(nums)
            .containsExactly(1000, 10000, 100000)
    }
}