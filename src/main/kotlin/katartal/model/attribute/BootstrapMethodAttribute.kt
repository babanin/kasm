package katartal.model.attribute

import katartal.model.CPoolIndex
import katartal.util.DynamicByteArray

/**
 * BootstrapMethods_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 num_bootstrap_methods;
 *     {   u2 bootstrap_method_ref;
 *         u2 num_bootstrap_arguments;
 *         u2 bootstrap_arguments[num_bootstrap_arguments];
 *     } bootstrap_methods[num_bootstrap_methods];
 * }
 */
class BootstrapMethodAttribute(attributeNameIndex: CPoolIndex, val bootstrapMethods: List<BootstrapMethod>) :
    Attribute(attributeNameIndex), ClassAttribute {
    override fun generateAttributeData(): ByteArray {
        return DynamicByteArray().apply {
            putU2(bootstrapMethods.size)
            for (method in bootstrapMethods) {
                putU2(method.bootstrapMethodRef.toUInt())

                putU2(method.bootstrapArguments.size)
                for (argument in method.bootstrapArguments) {
                    putU2(argument.toUInt())
                }
            }
        }.toByteArray()
    }
}

data class BootstrapMethod(
    val bootstrapMethodRef: CPoolIndex,
    val bootstrapArguments: List<CPoolIndex>,
)