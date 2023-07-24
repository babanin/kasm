package katartal.generators

import katartal.model.cls.CommonClassBuilder

interface ClassGenerator {
    fun toByteArray(clsBuilder: CommonClassBuilder<*>): ByteArray
}