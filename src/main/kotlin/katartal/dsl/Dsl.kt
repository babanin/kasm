package katartal.dsl

import katartal.model.cls.*
import katartal.model.cls.ClassAccess.Companion.PUBLIC

fun _class(name: String, init: ClassBuilder.() -> Unit = {}): ClassBuilder {
    val classBuilder = ClassBuilder(name, PUBLIC)
    classBuilder.init()
    classBuilder.flush()
    return classBuilder
}

fun _annotation(name: String, init: AnnotationBuilder.() -> Unit = {}): AnnotationBuilder {
    val classBuilder = AnnotationBuilder(name, PUBLIC)
    classBuilder.init()
    classBuilder.flush()
    return classBuilder
}

fun _interface(name: String, init: InterfaceBuilder.() -> Unit = {}): InterfaceBuilder {
    val classBuilder = InterfaceBuilder(name, PUBLIC)
    classBuilder.init()
    classBuilder.flush()
    return classBuilder
}

fun _enum(name: String, init: EnumBuilder.() -> Unit = {}): EnumBuilder {
    val classBuilder = EnumBuilder(name, PUBLIC)
    classBuilder.init()
    classBuilder.flush()
    return classBuilder
}
