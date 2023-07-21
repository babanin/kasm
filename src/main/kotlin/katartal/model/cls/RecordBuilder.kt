package katartal.model.cls

import katartal.model.ByteCode
import katartal.model.ConstantPool.RefKind.REF_getField
import katartal.model.ConstantPool.RefKind.REF_invokeStatic
import katartal.model.attribute.RecordAttribute
import katartal.model.attribute.RecordComponentInfo
import katartal.model.field.FieldAccess
import katartal.util.descriptor
import katartal.util.path

class RecordBuilder(
    name: String,
    parameters: List<Pair<String, Any>> = listOf(),
    access: ClassAccess = ClassAccess.PUBLIC
) :
    CommonClassBuilder<RecordBuilder>(name, access + ClassAccess.FINAL + ClassAccess.SUPER, "java/lang/Record") {

    private data class Field(val name: String, val type: String)

    private val fields: List<Field>

    init {
        fields = parameters.map {
            Field(
                it.first, when (it.second) {
                    is Class<*> -> (it.second as Class<*>).descriptor()
                    else -> it.second.toString()
                }
            )
        }

        fields.forEach { _field(it.name, it.type, FieldAccess.PRIVATE + FieldAccess.FINAL) }

        for (field in fields) {
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

        _constructor(parameters) {
            _code {
                _instruction(ByteCode.ALOAD_0)

                _instruction(ByteCode.INVOKESPECIAL) {
                    _indexU2(constantPool.writeMethodRef("java/lang/Record", "<init>", "()V"))
                }

                for (field in fields) {
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
            "java/lang/runtime/ObjectMethods",
            "bootstrap",
            "(Ljava/lang/invoke/MethodHandles\$Lookup;Ljava/lang/String;Ljava/lang/invoke/TypeDescriptor;Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/invoke/MethodHandle;)Ljava/lang/Object;"
        ) {
            _class(className)
            _string(fields.joinToString(";") { it.name })

            fields.forEach {
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
        attributes += buildRecordAttribute()
        
        super.flush()
    }

    private fun buildRecordAttribute(): RecordAttribute {
        val components = fields.map {
            RecordComponentInfo(constantPool.writeUtf8(it.name), constantPool.writeUtf8(it.type), listOf())
        }

        return RecordAttribute(constantPool.writeUtf8("Record"), components)
    }
}