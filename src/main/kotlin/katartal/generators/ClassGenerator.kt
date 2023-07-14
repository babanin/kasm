package katartal.generators

import katartal.model.ClassBuilder

interface ClassGenerator {
    fun toByteArray(clsBuilder: ClassBuilder) : ByteArray
}