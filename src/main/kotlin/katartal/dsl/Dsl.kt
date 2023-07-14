package katartal.dsl

import katartal.model.ClassBuilder
import katartal.model.ClassAccess

fun _class(name: String, init: ClassBuilder.() -> Unit = {}): ClassBuilder {
    val classBuilder = ClassBuilder(name, ClassAccess.PUBLIC)
    classBuilder.init()
    return classBuilder
}
