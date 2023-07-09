package katartal.generators.asm

import katartal.generators.ClassGenerator
import katartal.model._Class
import katartal.model._Method
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor

class AsmClassGenerator : ClassGenerator {
    override fun toByteArray(cls: _Class): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        cw.visit(cls.version.opcode, cls.access.opcode, cls.name, null, cls.parent, cls.implements.toTypedArray())

        cls.methods.forEach {
            val mv = cw.visitMethod(it.access.opcode, it.name, null, null, emptyArray<String>())
            it.instructions.forEach { 
                mv.visitMethodInsn()
            }
            mv.visitEnd()
        }

        return cw.toByteArray()
    }

}