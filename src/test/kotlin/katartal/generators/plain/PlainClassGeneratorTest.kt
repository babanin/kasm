package katartal.generators.plain

import katartal.dsl._class
import katartal.util.ByteArrayClassLoader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import java.io.Serializable

class PlainClassGeneratorTest {
    @Test
    fun shouldGenerateEmptyValidClass() {
        // given
        val klass = _class("Test")

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass).isNotNull;
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

        assertThat(toClass).isNotNull;
        assertThat(toClass.superclass).isEqualTo(ArrayList::class.java)
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

        assertThat(toClass).isNotNull
        assertThat(toClass.interfaces)
            .isNotNull
            .isNotEmpty
            .containsExactly(Serializable::class.java)
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

        assertThat(toClass).isNotNull

        val constructor = toClass.getConstructor(String::class.java)
        assertThat(constructor).isNotNull
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

        assertThat(toClass).isNotNull
        assertThat(toClass).hasDeclaredMethods("equals")
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

        assertThat(toClass).isNotNull;
    }
}