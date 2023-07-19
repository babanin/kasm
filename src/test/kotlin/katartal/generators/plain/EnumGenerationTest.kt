package katartal.generators.plain

import katartal.dsl._enum
import katartal.model.ByteCode
import katartal.model.ByteCode.SIPUSH
import katartal.model.field.FieldAccess
import katartal.util.ByteArrayClassLoader
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import util.assertThat
import java.io.File
import java.io.FileOutputStream

class EnumGenerationTest {
    /**
     *public enum EmptyEnum {
     *}
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

        val valuesMethod = toClass.getDeclaredMethod("values")
        var result = valuesMethod.invoke(null) as Array<*>
        for (any in result) {
            println(any)
        }
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
    @Disabled
    fun shouldGenerateEnumWithFields() {
        // given
        val klass = _enum("EnumWithFields") {
            _value("A") {
                _instruction(SIPUSH) {
                    _value(1000)
                }
            }

            _value("B") {
                _instruction(SIPUSH) {
                    _value(10000)
                }
            }

            _value("C") {
                _ldc(100_000)
            }

            _field("num", Int::class.java, FieldAccess.PRIVATE)

            _constructor(listOf("name" to String::class.java, "ordinal" to Int::class.java, "num" to Int::class.java)) {
                _code {
                    _instruction(ByteCode.ALOAD_0)
                    _instruction(ByteCode.ALOAD_1)
                    _instruction(ByteCode.ILOAD_2)
                    _invokeSpecial(Enum::class.java, "<init>", "(Ljava/lang/String;I)V")

                    _instruction(ByteCode.ALOAD_0)
                    _instruction(ByteCode.ILOAD_3)
                    _instruction(ByteCode.PUTFIELD) {

                    }

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
            .isEnum()
    }
}