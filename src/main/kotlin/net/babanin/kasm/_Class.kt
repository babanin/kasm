package net.babanin.kasm

import net.babanin.kasm.util.ByteArrayClassLoader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

fun <T> Class<T>.path() :String {
    val pkg = this.`package`
    return pkg.name.replace(".", "/") + "/" + this.simpleName
}

enum class JavaVersion(val opcode: Int) {
    V1(Opcodes.V1_1),
    V2(Opcodes.V1_2),
    V3(Opcodes.V1_3),
    V4(Opcodes.V1_4),
    V5(Opcodes.V1_5),
    V6(Opcodes.V1_6),
    V7(Opcodes.V1_7),
    V8(Opcodes.V1_8),
    V9(Opcodes.V9),
    V10(Opcodes.V10),
    V11(Opcodes.V11),
    V12(Opcodes.V12),
    V13(Opcodes.V13),
    V14(Opcodes.V14),
    V15(Opcodes.V15),
    V16(Opcodes.V16),
    V17(Opcodes.V17),
    V18(Opcodes.V18),
    V19(Opcodes.V19),
    V20(Opcodes.V20),
    V21(Opcodes.V21)
}

data class Access(val opcode: Int) {
    fun plus(access: Access): Access {
        return Access(opcode + access.opcode)
    }

    companion object {
        val PUBLIC = Access(Opcodes.ACC_PUBLIC)
        val PRIVATE = Access(Opcodes.ACC_PRIVATE)
        val PROTECTED = Access(Opcodes.ACC_PROTECTED)
        val STATIC = Access(Opcodes.ACC_STATIC)
        val FINAL = Access(Opcodes.ACC_FINAL)
        val SUPER = Access(Opcodes.ACC_SUPER)
        val SYNCHRONIZED = Access(Opcodes.ACC_SYNCHRONIZED)
        val OPEN = Access(Opcodes.ACC_OPEN)
        val TRANSITIVE = Access(Opcodes.ACC_TRANSITIVE)
        val VOLATILE = Access(Opcodes.ACC_VOLATILE)
        val BRIDGE = Access(Opcodes.ACC_BRIDGE)
        val STATIC_PHASE = Access(Opcodes.ACC_STATIC_PHASE)
        val VARARGS = Access(Opcodes.ACC_VARARGS)
        val TRANSIENT = Access(Opcodes.ACC_TRANSIENT)
        val NATIVE = Access(Opcodes.ACC_NATIVE)
        val INTERFACE = Access(Opcodes.ACC_INTERFACE)
        val ABSTRACT = Access(Opcodes.ACC_ABSTRACT)
        val STRICT = Access(Opcodes.ACC_STRICT)
        val SYNTHETIC = Access(Opcodes.ACC_SYNTHETIC)
        val ANNOTATION = Access(Opcodes.ACC_ANNOTATION)
        val ENUM = Access(Opcodes.ACC_ENUM)
        val MANDATED = Access(Opcodes.ACC_MANDATED)
        val MODULE = Access(Opcodes.ACC_MODULE)
    }
}

data class _Method(val access: Access, 
                   val name: String, 
                   val returns: String,
                   val throws: List<String> = listOf()) {


    infix fun throws(interfaceCls: String): _Method {
        return copy(throws = this.throws + interfaceCls)
    }

    infix fun <T : Any> throws(interfaceCls: Class<T>): _Method {
        return copy(throws = this.throws + interfaceCls.path())
    }

    infix fun <T : Any> throws(interfaceClasses: List<String>): _Method {
        return copy(throws = this.throws + interfaceClasses)
    }

    infix fun <T : Any> throws(interfaceClasses: List<Class<T>>): _Method {
        return copy(throws = this.throws + interfaceClasses.map { it.path() })
    }
    
    fun generate(mv: MethodVisitor) {

    }
}

data class _Class(
    val name: String,
    val access: Access,
    val parent: String = "java/lang/Object",
    val implements: List<String> = listOf(),
    val methods: List<_Method>,
    val version: JavaVersion = JavaVersion.V8
) {
    fun toByteArray(): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        cw.visit(version.opcode, access.opcode, name, null, parent, implements.toTypedArray())

        methods.forEach {
            val mv = cw.visitMethod(it.access.opcode, it.name, null, )
            it.generate(mv)
            mv.visitEnd()
        }

        return cw.toByteArray()
    }

    infix fun extends(parentCls: String): _Class {
        return copy(parent = parentCls)
    }

    infix fun <T : Any> extends(parentCls: Class<T>): _Class {
        return copy(parent = parentCls.path())
    }

    infix fun implements(interfaceCls: String): _Class {
        return copy(implements = this.implements + interfaceCls)
    }

    infix fun <T : Any> implements(interfaceCls: Class<T>): _Class {
        return copy(implements = this.implements + interfaceCls.path())
    }

    infix fun <T : Any> implements(interfaceClasses: List<String>): _Class {
        return copy(implements = this.implements + interfaceClasses)
    }

    infix fun <T : Any> implements(interfaceClasses: List<Class<T>>): _Class {
        return copy(implements = this.implements + interfaceClasses.map { it.path() })
    }
    fun toClass(classLoader: ClassLoader = this.javaClass.classLoader): Class<*> {
        return ByteArrayClassLoader(classLoader).loadClass(name, toByteArray())
    }
}

class MethodBuilder(val name: String = "<init>", val ctr: Boolean = false) {
    fun _return(boolean: Boolean) {
        
    }

    fun build(): _Method {
        return _Method(KlassAccess.PUBLIC, name)
    }
}

class ClassBuilder(
    val name: String, var access: KlassAccess, var parent: String = "java/lang/Object"
) {
    private val methodBuilders: MutableList<MethodBuilder> = mutableListOf()

    fun _constructor(parameters: List<Pair<String, Any>> = listOf(), init: MethodBuilder.() -> Unit): MethodBuilder {
        val methodBuilder = MethodBuilder(name, ctr = true)
        methodBuilders.add(methodBuilder)
        methodBuilder.init()
        return methodBuilder
    }

    fun _method(
        name: String, 
        parameters: List<Pair<String, Any>> = listOf(), 
        returns: Any = Void::class.java,        
        init: MethodBuilder.() -> Unit
    ): _Method {
        val methodBuilder = MethodBuilder(name)
        methodBuilders.add(methodBuilder)

        methodBuilder.init()
        return methodBuilder.build()
    }

    fun build(): _Class {
        val methods = methodBuilders.map { it.generate() }
        return _Class(name, access, parent, methods)
    }
}