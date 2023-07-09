package katartal.generators

import katartal.model._Class

interface ClassGenerator {
    fun toByteArray(cls: _Class) : ByteArray
}