package katartal.generators.plain

import katartal.dsl._record
import katartal.util.ByteArrayClassLoader
import org.junit.jupiter.api.Test
import util.Assertions.assertThat
import java.io.File
import java.io.FileOutputStream

class RecordGenerationTest {
    /**
     * public record Point(int x, int y) {
     * }
     */
    @Test
    fun shouldGenerateRecord() {
        // given
        val klass = _record("Point") {
            _component("x", Int::class.java)
            _component("y", Int::class.java)
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .isRecord()

        assertThat(toClass.recordComponents)
            .hasSize(2)
    }

    /**
     * public record Empty() {
     * }
     */
    @Test
    fun shouldGenerateEmptyRecord() {
        // given
        val klass = _record("Point")

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        assertThat(toClass)
            .isNotNull
            .isRecord()

        assertThat(toClass.recordComponents)
            .isEmpty()
    }

    fun print(array: ByteArray) {
        val fop = FileOutputStream(File("Test.class"))
        fop.write(array)
        fop.close()
    }
}