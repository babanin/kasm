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
    val annotationBuilder = AnnotationBuilder(name, PUBLIC)
    annotationBuilder.init()
    annotationBuilder.flush()
    return annotationBuilder
}

fun _interface(name: String, init: InterfaceBuilder.() -> Unit = {}): InterfaceBuilder {
    val interfaceBuilder = InterfaceBuilder(name, PUBLIC)
    interfaceBuilder.init()
    interfaceBuilder.flush()
    return interfaceBuilder
}

fun _enum(name: String, init: EnumBuilder.() -> Unit = {}): EnumBuilder {
    val enumBuilder = EnumBuilder(name, PUBLIC)
    enumBuilder.init()
    enumBuilder.flush()
    return enumBuilder
}

fun _record(
    name: String, init: RecordBuilder.() -> Unit = {}
): RecordBuilder {
    val recordBuilder = RecordBuilder(name, PUBLIC)
    recordBuilder.init()
    recordBuilder.flush()
    return recordBuilder
}