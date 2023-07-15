package katartal.generators.plain

import katartal.dsl._enum
import katartal.util.ByteArrayClassLoader
import org.junit.jupiter.api.Test
import util.assertThat

class EnumGenerationTest {
    @Test
    fun shouldGenerateEmptyValidClass() {
        // given
        val klass = _enum("Test")

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass).isNotNull;
        assertThat(toClass).isEnum()
    }
}