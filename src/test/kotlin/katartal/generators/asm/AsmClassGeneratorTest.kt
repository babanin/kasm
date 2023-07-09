package katartal.generators.asm

import katartal.dsl.klass
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import java.io.Serializable

class AsmClassGeneratorTest {

    @Test
    fun toByteArray() {
        val klass = klass("Test") {
            constructor(listOf("name" to String::class.java)) { }

            method(name = "equals", parameters = listOf("other" to Object::class.java), returns = Boolean::class.java) {
                _codes { 
                    
                }

                _return(true)
            } throws FileNotFoundException::class.java

        } implements Serializable::class.java

        val toClass = klass.toClass()
        val instance = toClass.getConstructor(String::class.java).newInstance("name")
        println(instance.equals("name"))
    }
}