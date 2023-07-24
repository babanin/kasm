package katartal.model.method

class ExceptionBuilder(val currentPos: UShort) {
    private var tryBlock : CodeBuilder? = null
    private val catchBlocks = mutableListOf<CodeBuilder>()

    fun _try(block: CodeBuilder.() -> Unit) : CodeBuilder {
        val codeBuilder = CodeBuilder()
        this.tryBlock = codeBuilder
        return codeBuilder
    }
    
    fun _catch(vararg exception: Class<*>, block: CodeBuilder.() -> Unit) : CodeBuilder {
        val codeBuilder = CodeBuilder()
        catchBlocks += codeBuilder
        return codeBuilder
    }


    fun _catch(vararg exception: String, block: CodeBuilder.() -> Unit) : CodeBuilder {
        val codeBuilder = CodeBuilder()
        catchBlocks += codeBuilder
        return codeBuilder
    }
}