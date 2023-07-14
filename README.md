# Katartal

Experiments Kotlin DSL for JVM class generation

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