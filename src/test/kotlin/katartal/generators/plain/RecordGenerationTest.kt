package katartal.generators.plain

import katartal.dsl._enum
import katartal.dsl._record
import katartal.model.ByteCode
import katartal.model.ByteCode.*
import katartal.model.field.FieldAccess
import katartal.util.ByteArrayClassLoader
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import util.assertThat
import java.io.File
import java.io.FileOutputStream

class RecordGenerationTest {
    /**
     * public enum EmptyEnum {
     * }
     */
    @Test
    fun shouldGenerateRecord() {
        // given
        val klass = _record("Point", listOf("x" to Int::class.java, "y" to Int::class.java))

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)
        print(clsBytes)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .isRecord()
    }

    fun print(array: ByteArray) {
        val fop = FileOutputStream(File("Test.class"))
        fop.write(array)
        fop.close()
    }
}