package katartal.model.field

import katartal.model.CPoolIndex
import katartal.model.ConstantPool
import katartal.model.attribute.ConstantValueAttribute

class StaticFieldBuilder(
    name: String,
    descriptor: String,
    access: FieldAccess,
    private val constantPool: ConstantPool
) : FieldBuilder(name, descriptor, access + FieldAccess.STATIC, constantPool) {
    private var constantIdx: CPoolIndex? = null

    private fun typeMismatchMessage(actualFriendly: String, actual: String, value: Any): String {
        return "$actualFriendly value $value provided while field type is $descriptor. " +
                "Change type to '$actual'."
    }

    fun _value(num: Int) {
        if (descriptor != "I") {
            throw IllegalStateException(typeMismatchMessage("Int", "I", num))
        }
        
        constantIdx = constantPool.writeInteger(num)
    }

    fun _value(value: Boolean) {
        if (descriptor != "Z") {
            throw IllegalStateException(typeMismatchMessage("Boolean", "Z", value))
        }
        
        constantIdx = constantPool.writeInteger(if (value) 1 else 0)
    }

    fun _value(value: Char) {
        if (descriptor != "C") {
            throw IllegalStateException(typeMismatchMessage("Char", "C", value))
        }
        
        constantIdx = constantPool.writeInteger(value.code)
    }

    fun _value(num: Float) {
        if (descriptor != "F") {
            throw IllegalStateException(typeMismatchMessage("Float", "F", num))
        }

        constantIdx = constantPool.writeFloat(num)
    }

    fun _value(num: Long) {
        if (descriptor != "J") {
            throw IllegalStateException(typeMismatchMessage("Long", "J", num))
        }

        constantIdx = constantPool.writeLong(num)
    }

    fun _value(num: Double) {
        if (descriptor != "D") {
            throw IllegalStateException(typeMismatchMessage("Double", "D", num))
        }

        constantIdx = constantPool.writeDouble(num)
    }

    fun _value(value: String) {
        if (descriptor != "Ljava/lang/String;") {
            throw IllegalStateException(
                typeMismatchMessage(
                    "String",
                    "Ljava/lang/String",
                    value
                )
            )
        }

        constantIdx = constantPool.writeString(value)
    }

    override fun flush() {
        if (constantIdx != null) {
            attributes += ConstantValueAttribute(constantPool.writeUtf8("ConstantValue"), constantIdx!!)
        }

        super.flush()
    }
}