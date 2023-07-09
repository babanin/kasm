package katartal.dsl

import katartal.model.ClassBuilder
import katartal.model.KlassAccess
import katartal.model._Class

fun klass(name: String, init: ClassBuilder.() -> Unit): _Class {
    val classBuilder = ClassBuilder(name, KlassAccess.PUBLIC)
    classBuilder.init()
    return classBuilder.build()
}
