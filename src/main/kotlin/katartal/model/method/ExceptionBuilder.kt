package katartal.model.method

import katartal.model.ConstantPool
import java.lang.IllegalStateException

class ExceptionBuilder(currentPos: UShort, constantPool: ConstantPool, labels: MutableMap<String, Label>) :
    CodeBuilder(initialOffset = currentPos, constantPool = constantPool, labels = labels) {
    private var tryBlock: CodeBuilder? = null
    private val catchBlocks = mutableListOf<CodeBuilder>()

    fun _try(block: CodeBuilder.() -> Unit): CodeBuilder {
        if(tryBlock != null) throw IllegalStateException("Try block is already defined. Only one is allowed.")
        
        val codeBuilder = CodeBuilder(initialOffset = currentPos, constantPool = constantPool, labels = labels)
        codeBuilder.block()
        this.tryBlock = codeBuilder
        return codeBuilder
    }

    fun _catch(vararg exception: Class<*>, block: CodeBuilder.() -> Unit): CodeBuilder {
        val codeBuilder = CodeBuilder(initialOffset = currentPos, constantPool = constantPool, labels = labels)
        codeBuilder.block()
        catchBlocks += codeBuilder
        return codeBuilder
    }


    fun _catch(vararg exception: String, block: CodeBuilder.() -> Unit): CodeBuilder {
        val codeBuilder = CodeBuilder(initialOffset = currentPos, constantPool = constantPool, labels = labels)
        codeBuilder.block()
        catchBlocks += codeBuilder
        return codeBuilder
    }

     fun flush() :  {
        if (tryBlock != null) {
            this.plus(tryBlock!!)
        }
        
        catchBlocks.fold(this as CodeBuilder) { acc, cb -> acc.plus(cb) }

        super.flush()
    }
}