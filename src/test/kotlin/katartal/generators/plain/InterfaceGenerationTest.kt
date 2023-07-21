package katartal.generators.plain

import katartal.dsl._interface
import katartal.model.method.MethodAccess
import katartal.util.ByteArrayClassLoader
import org.junit.jupiter.api.Test
import util.Assertions

class InterfaceGenerationTest {
    /**
     * public interface Run {
     *     void run();
     * }
     */
    @Test
    fun shouldGenerateSamInterface() {
        // given
        val klass = _interface("Run") {
            _method("run", access = MethodAccess.PUBLIC + MethodAccess.ABSTRACT)
            
            _annotate(FunctionalInterface::class.java)
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.className, clsBytes)

        Assertions.assertThat(toClass)
            .isNotNull
            .isInterface
            .hasDeclaredMethods("run")
    }

}