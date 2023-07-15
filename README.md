# Katartal

Experiments Kotlin DSL for JVM class generation

## Examples

### Hello world!
```kotlin
_class("Test") {
    _method("main", listOf("args" to Array<String>::class.java), PUBLIC + STATIC) {
        _code {
            _getstatic(System::class.java, "out", PrintStream::class.java)
            _ldc("Hello world!")
            _invokeVirtual(PrintStream::class.java, "println", "(Ljava/lang/String;)V")
            _return()
        }
    }
}
```

### Misc

```kotlin
_class("Test") {

    _constructor(listOf("name" to String::class.java)) { }
    
    _method(
        name = "equals",
        parameters = listOf("other" to Object::class.java),
        returns = Boolean::class.java
    ) {
        _return(true)
    } throws FileNotFoundException::class.java
    
} extends ArrayList::class.java implements Serializable::class.java
```