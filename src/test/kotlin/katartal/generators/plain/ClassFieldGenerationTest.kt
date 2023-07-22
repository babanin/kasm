package katartal.generators.plain

import katartal.dsl._class
import katartal.model.field.FieldAccess.Companion.FINAL
import katartal.model.field.FieldAccess.Companion.PRIVATE
import katartal.model.field.FieldAccess.Companion.PROTECTED
import katartal.model.field.FieldAccess.Companion.STATIC
import katartal.model.field.FieldAccess.Companion.VOLATILE
import katartal.util.ByteArrayClassLoader
import katartal.util.descriptor
import org.junit.jupiter.api.Test
import util.Assertions.assertThat
import java.io.File
import java.io.FileOutputStream

class ClassFieldGenerationTest {
    @Test
    fun shouldGenerateClassWithFields() {
        // given
        val klass = _class("Test") {
            _field("primitiveLong", Long::class.java.descriptor(), PRIVATE + FINAL)
            _field("boxedLong", java.lang.Long::class.java.descriptor(), PRIVATE + VOLATILE)

            _field("arrayOfInt", IntArray::class.java, PROTECTED + STATIC)

            _staticField("primitiveInt", Int::class.java) {
                _value(1000)
            }

            _staticField("primitiveBoolean", Boolean::class.java) {
                _value(true)
            }

            _staticField("primitiveChar", Char::class.java) {
                _value('c')
            }

            _staticField("staticFloat", Float::class.java) {
                _value(1.0F)
            }
            
            _staticField("staticLong", Long::class.java) {
                _value(1000L)
            }

            _staticField("staticDouble", Double::class.java) {
                _value(1000.0)
            }

            _staticField("str", String::class.java) {
                _value("hello world")
            }
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val loadedClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(loadedClass)
            .isNotNull
            .hasFieldWithType("primitiveLong", Long::class.java)
            .hasFieldWithType("boxedLong", java.lang.Long::class.java)
            .hasFieldWithType("arrayOfInt", IntArray::class.java)
    }

    fun print(array: ByteArray) {
        val fop = FileOutputStream(File("Test.class"))
        fop.write(array)
        fop.close()
    }
}