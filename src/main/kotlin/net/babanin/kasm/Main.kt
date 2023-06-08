package net.babanin.kasm

import java.io.FileNotFoundException
import java.io.Serializable


fun _class(name: String, init: ClassBuilder.() -> Unit): _Class {
    val classBuilder = ClassBuilder(name, KlassAccess.PUBLIC)
    classBuilder.init()
    return classBuilder.build()
}


fun main() {
    val klass = _class("Test") {
        _constructor(listOf("name" to String::class.java)) { }

        _method(name = "equals", parameters = listOf("other" to Object::class.java), returns = Boolean::class.java) {
            _return()
        } throws FileNotFoundException::class.java
        
    } implements Serializable::class.java

    val toClass = klass.toClass()
    val instance = toClass.getConstructor(String::class.java).newInstance("name")
    println(instance.equals("name"))
}