package net.babanin.kasm.util

class ByteArrayClassLoader(parent: ClassLoader?) : ClassLoader(parent) {
    fun loadClass(name: String, b: ByteArray): Class<*> {
        return defineClass(name, b, 0, b.size);
    }
}