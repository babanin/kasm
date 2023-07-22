package katartal.model.cls

import katartal.model.ByteCode
import katartal.model.ConstantPool.RefKind.REF_getField
import katartal.model.ConstantPool.RefKind.REF_invokeStatic
import katartal.model.attribute.RecordAttribute
import katartal.model.attribute.RecordComponentInfo
import katartal.model.field.FieldAccess
import katartal.model.field.FieldBuilder
import katartal.util.descriptor
import katartal.util.methodDescriptor
import katartal.util.path
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles.Lookup
import java.lang.invoke.TypeDescriptor
import java.lang.runtime.ObjectMethods

class RecordBuilder(
    name: String,
    access: ClassAccess = ClassAccess.PUBLIC
) :
    CommonClassBuilder<RecordBuilder>(name, access + ClassAccess.FINAL + ClassAccess.SUPER, "java/lang/Record") {

    private val components = mutableListOf<Component>()

    private data class Component(val name: String, val type: String)

    fun _component(name: String, type: String, init: FieldBuilder.() -> Unit = {}): FieldBuilder {
        components += Component(name, type)
        return _field(name, type, FieldAccess.PRIVATE + FieldAccess.FINAL, init)
    }

    fun _component(name: String, type: Class<*>, init: FieldBuilder.() -> Unit = {}): FieldBuilder {
        return _component(name, type.descriptor(), init)
    }

    infix fun implements(interfaceCls: String): RecordBuilder {
        this.implements += constantPool.writeClass(interfaceCls)
        return this
    }

    infix fun <T : Any> implements(interfaceCls: Class<T>): RecordBuilder {
        this.implements += constantPool.writeClass(interfaceCls.path())
        return this
    }

    override fun flush() {
        for (field in components) {
            _method(field.name) {
                _code {
                    _instruction(ByteCode.ALOAD_0)

                    _instruction(ByteCode.GETFIELD) {
                        _indexU2(constantPool.writeFieldRef(className, field.name, field.type))
                    }

                    _return(field.type)

                }
            } returns field.type

        }

        _constructor(components.map { Pair(it.name, it.type) }) {
            _code {
                _instruction(ByteCode.ALOAD_0)

                _instruction(ByteCode.INVOKESPECIAL) {
                    _indexU2(constantPool.writeMethodRef("java/lang/Record", "<init>", "()V"))
                }

                for (field in components) {
                    _instruction(ByteCode.ALOAD_0)
                    _instruction(ByteCode.ILOAD_1)
                    _instruction(ByteCode.PUTFIELD) {
                        _indexU2(constantPool.writeFieldRef(className, field.name, field.type))
                    }
                }

                _return()
            }
        }

        val objectMethodsBootstrap = _bootstrapMethods(
            REF_invokeStatic,
            ObjectMethods::class.java.path(),
            "bootstrap",
            methodDescriptor(
                returnType = Object::class.java,
                Lookup::class.java,
                String::class.java,
                TypeDescriptor::class.java,
                Class::class.java,
                String::class.java,
                Array<MethodHandle>::class.java
            )
        ) {
            _class(className)
            _string(components.joinToString(";") { it.name })

            components.forEach {
                _methodHandle(REF_getField, className, it.name, it.type)
            }
        }

        _method("hashCode") {
            _code {
                _instruction(ByteCode.ALOAD_0)

                _instruction(ByteCode.INVOKEDYNAMIC) {
                    _indexU2(
                        constantPool.writeInvokeDynamic(
                            objectMethodsBootstrap.index,
                            "hashCode",
                            "(L$className;)I"
                        )
                    )
                    _indexU2(0u)
                }

                _instruction(ByteCode.IRETURN)

            }
        } returns Int::class.java

        _method("toString") {
            _code {
                _instruction(ByteCode.ALOAD_0)

                _instruction(ByteCode.INVOKEDYNAMIC) {
                    _indexU2(
                        constantPool.writeInvokeDynamic(
                            objectMethodsBootstrap.index,
                            "toString",
                            "(L$className;)Ljava/lang/String;"
                        )
                    )
                    _indexU2(0u)
                }
            }
        } returns String::class.java

        _method("equals", listOf("other" to Object::class.java)) {
            _code {
                _instruction(ByteCode.ALOAD_0)
                _instruction(ByteCode.ALOAD_1)

                _instruction(ByteCode.INVOKEDYNAMIC) {
                    _indexU2(
                        constantPool.writeInvokeDynamic(
                            objectMethodsBootstrap.index,
                            "equals",
                            "(L$className;Ljava/lang/Object;)Z"
                        )
                    )
                    _indexU2(0u)
                }
            }
        } returns Boolean::class.java

        attributes += buildRecordAttribute()

        super.flush()
    }

    private fun buildRecordAttribute(): RecordAttribute {
        val components = components.map {
            RecordComponentInfo(constantPool.writeUtf8(it.name), constantPool.writeUtf8(it.type), listOf())
        }

        return RecordAttribute(constantPool.writeUtf8("Record"), components)
    }
}