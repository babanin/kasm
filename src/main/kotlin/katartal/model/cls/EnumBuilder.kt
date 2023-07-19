package katartal.model.cls

import katartal.model.ByteCode
import katartal.model.field.FieldAccess.Companion.ENUM
import katartal.model.field.FieldAccess.Companion.FINAL
import katartal.model.field.FieldAccess.Companion.PRIVATE
import katartal.model.field.FieldAccess.Companion.PUBLIC
import katartal.model.field.FieldAccess.Companion.STATIC
import katartal.model.field.FieldAccess.Companion.SYNTHETIC
import katartal.model.method.CodeBuilder
import katartal.model.method.MethodAccess
import katartal.util.path

class EnumBuilder(enumName: String, access: ClassAccess) :
    CommonClassBuilder<EnumBuilder>(enumName, access + ClassAccess.ENUM, "java/lang/Enum") {
    infix fun implements(interfaceCls: String): EnumBuilder {
        this.implements += constantPool.writeClass(interfaceCls)
        return this
    }

    infix fun <T : Any> implements(interfaceCls: Class<T>): EnumBuilder {
        this.implements += constantPool.writeClass(interfaceCls.path())
        return this
    }

    fun _value(name: String, ctrCode: CodeBuilder.() -> Unit = {}) {
        _field(name, "L${className};", PUBLIC + STATIC + FINAL + ENUM)
    }

    override fun flush() {
        _field("\$VALUES", "[L$className;", PRIVATE + STATIC + FINAL + SYNTHETIC)

        val ctor =
            _constructor(listOf("name" to String::class.java, "ordinal" to Int::class.java), MethodAccess.PRIVATE) {
                _code {
                    _instruction(ByteCode.ALOAD_0)
                    _instruction(ByteCode.ALOAD_1)
                    _instruction(ByteCode.ILOAD_2)

                    _instruction(ByteCode.INVOKESPECIAL) {
                        _indexU2(constantPool.writeMethodRef("java/lang/Enum", "<init>", "(Ljava/lang/String;I)V"))
                    }

                    _return()
                }
            }

        // Skip $VALUES field
        val nonSyntheticFields = fieldBuilders.filter { !it.access[SYNTHETIC] }

        _method("\$values", access = MethodAccess.PRIVATE + MethodAccess.STATIC + MethodAccess.SYNTHETIC) {
            _code {
                _iconst(nonSyntheticFields.size)
                _instruction(ByteCode.ANEWARRAY) {
                    _indexU2(constantPool.writeClass(className))
                }

                for (field in nonSyntheticFields.withIndex()) {
                    _instruction(ByteCode.DUP)
                    _iconst(field.index)
                    _getstatic(className, field.value.name, "L${className};")
                    _instruction(ByteCode.AASTORE)
                }

                _instruction(ByteCode.ARETURN)
            }
        } returns "[L$className;"

        _method("values", access = MethodAccess.PUBLIC + MethodAccess.STATIC) {
            _code {
                _getstatic(className, "\$VALUES", "[L$className;")

                _instruction(ByteCode.INVOKEVIRTUAL) {
                    _indexU2(constantPool.writeMethodRef("[L$className;", "clone", "()Ljava/lang/Object;"))
                }

                _instruction(ByteCode.CHECKCAST) {
                    _indexU2(constantPool.writeClass("[L$className;"))
                }

                _instruction(ByteCode.ARETURN)
            }
        } returns "[L$className;"

        val staticInitDefinedByUser = methodBuilders.find { it.name == "<clinit>" } != null
        if (!staticInitDefinedByUser) {
            _method("<clinit>", access = MethodAccess.STATIC) {
                for (field in nonSyntheticFields.withIndex()) {
                    _code {
                        _instruction(ByteCode.NEW) {
                            _indexU2(constantPool.writeClass(className))
                        }
                        _instruction(ByteCode.DUP)
                        _ldc(field.value.name)
                        _iconst(field.index)

                        _instruction(ByteCode.INVOKESPECIAL) {
                            _indexU2(constantPool.writeMethodRef(className, "<init>", ctor.descriptorCpIndex))
                        }

                        _instruction(ByteCode.PUTSTATIC) {
                            _indexU2(constantPool.writeFieldRef(className, field.value.name, "L${className};"))
                        }
                    }
                }

                _code {
                    _instruction(ByteCode.INVOKESTATIC) {
                        _indexU2(constantPool.writeMethodRef(className, "\$values", "()[L$className;"))
                    }

                    _instruction(ByteCode.PUTSTATIC) {
                        _indexU2(constantPool.writeFieldRef(className, "\$VALUES", "[L$className;"))
                    }

                    _return()
                }
            }
        }

        super.flush()
    }
}