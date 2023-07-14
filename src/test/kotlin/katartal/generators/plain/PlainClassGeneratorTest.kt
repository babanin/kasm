package katartal.generators.plain

import katartal.dsl._class
import katartal.util.ByteArrayClassLoader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.Serializable

class PlainClassGeneratorTest {
    @Test
    fun shouldGenerateEmptyValidClass() {
        // given
        val klass = _class("Test") {    }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        val fileOutputStream = FileOutputStream(File("Test.class"))
        fileOutputStream.write(clsBytes)
        fileOutputStream.close()

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass).isNotNull;
    }
    
    @Test
    fun shouldGenerateValidClassWithImplementedMarkerInterface() {
        // given
        val klass = _class("Test") {
        } implements Serializable::class.java

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        val fileOutputStream = FileOutputStream(File("Test.class"))
        fileOutputStream.write(clsBytes)
        fileOutputStream.close()

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
            _constructor(listOf("name" to String::class.java)) { }
        } 

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        val fileOutputStream = FileOutputStream(File("Test.class"))
        fileOutputStream.write(clsBytes)
        fileOutputStream.close()

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass).isNotNull;
    }
    
    @Test
    fun shouldGenerateValidClassWithEqualsMethod() {
        // given
        val klass = _class("Test") {
            _method(
                name = "equals",
                parameters = listOf("other" to Object::class.java),
                returns = Boolean::class.java
            ) {
                _return(true)
            }
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        val fileOutputStream = FileOutputStream(File("Test.class"))
        fileOutputStream.write(clsBytes)
        fileOutputStream.close()

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass).isNotNull;
    }
    
    @Test
    fun shouldGenerateValidClassEqualsMethodThrowsException() {
        // given
        val klass = _class("Test") {
            _method(
                name = "equals",
                parameters = listOf("other" to Object::class.java),
                returns = Boolean::class.java
            ) {
                _return(true)
            } throws FileNotFoundException::class.java
        }

        // when
        val clsBytes = PlainClassGenerator().toByteArray(klass)

        val fileOutputStream = FileOutputStream(File("Test.class"))
        fileOutputStream.write(clsBytes)
        fileOutputStream.close()

        // then
        val classLoader = ByteArrayClassLoader(this.javaClass.classLoader)
        val toClass = classLoader.loadClass(klass.name, clsBytes)

        assertThat(toClass).isNotNull;
    }
}