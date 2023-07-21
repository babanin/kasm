package katartal.model.attribute

import katartal.model.CPoolIndex
import katartal.util.DynamicByteArray

/**
 * Record_attribute {
 *     u2                    attribute_name_index;
 *     u4                    attribute_length;
 *     u2                    components_count;
 *     record_component_info components[components_count];
 * }
 *
 * record_component_info {
 *     u2             name_index;
 *     u2             descriptor_index;
 *     u2             attributes_count;
 *     attribute_info attributes[attributes_count];
 * }
 */
class RecordAttribute(attributeNameIdx: CPoolIndex, val components: List<RecordComponentInfo>) :
    Attribute(attributeNameIdx) {

    override fun generateAttributeData(): ByteArray {
        return DynamicByteArray().apply {
            putU2(components.size)

            for (component in components) {
                putU2(component.nameIdx.toUInt())
                putU2(component.descriptorIdx.toUInt())

                putU2(component.attributes.size)
                for (attribute in component.attributes) {
                    putByteArray(attribute.toByteArray())
                }
            }
        }.toByteArray()
    }
}

data class RecordComponentInfo(val nameIdx: CPoolIndex, val descriptorIdx: CPoolIndex, val attributes: List<Attribute>)
