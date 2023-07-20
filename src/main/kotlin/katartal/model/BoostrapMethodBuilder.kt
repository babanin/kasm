package katartal.model

class BoostrapMethodBuilder(val kind: ConstantPool.RefKind, val cls: String, val name: String, val type: String, val index : UShort) {
    
    val args = mutableListOf<BootstrapMethodArgument>()
        
    fun _class(cls: String) {
        args += Cls(cls)
    }
    
    fun _string(value: String) {
        args += Str(cls)
    }
    
    fun _methodHandle(refKind: ConstantPool.RefKind, cls: String, name: String, type: String) {
        args += MH(refKind, cls, name, type)
    }
    
    sealed class BootstrapMethodArgument
    class Cls(val cls : String) : BootstrapMethodArgument()
    class Str(val value: String) : BootstrapMethodArgument()
    class MH(val refKind: ConstantPool.RefKind, val cls: String, val name: String, val type: String) : BootstrapMethodArgument()
    
}