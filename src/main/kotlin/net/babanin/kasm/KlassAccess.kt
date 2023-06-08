package net.babanin.kasm

import org.objectweb.asm.Opcodes

abstract class KlassAccess {
    fun toInt() : Int {
        return 0
    }
    
    companion object {
        val PUBLIC = object : KlassAccess() {}
    }
}
