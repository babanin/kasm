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

### FizzBuzz

Solution in Java:

```java
public class Test {
    public static String[] fizzBuzz(int count) {
        String[] result = new String[count];

        for (int i = 1; i <= count; i++) {
            if (i % 3 == 0 && i % 5 == 0) {
                result[i - 1] = "FizzBuzz";
            } else if (i % 3 == 0) {
                result[i - 1] = "Fizz";
            } else if (i % 5 == 0) {
                result[i - 1] = "Buzz";
            }
        }

        return result;
    }
}
```

Similar solution with Katartal:

```kotlin
_class("Test") {
    _method("fizzBuzz", listOf("count" to Int::class.java), PUBLIC + STATIC) {
        _locals {
            _var("result", Array<String>::class.java, 5u, 66u)
            _var("i", Int::class.java, 7u, 62u)
        }

        _code(maxLocals = 3, maxStack = 3) {
            // String[] result = new String[count]
            _instruction(ILOAD_0)
            _instruction(ANEWARRAY) {
                _referenceU2(constantPool.writeClass("java/lang/String"))
            }
            _instruction(ASTORE_1)

            // i = 1
            _instruction(ICONST_1)
            _instruction(ISTORE_2)

            _stackFrame {
                _append(ObjectVar(Array<String>::class.java), IntegerVar())
            }

            val forLabel = label()
            _instruction(ILOAD_2) // i
            _instruction(ILOAD_0) // count

            // i < count
            _if(IF_ICMPGT) {
                // i % 3 
                _mathOperation(IREM, ILOAD_2, ICONST_3)
                _if(IFNE) {
                    // i % 5 
                    _mathOperation(IREM, ILOAD_2, ICONST_5)
                    _if(IFNE) {
                        // then
                        _instruction(ALOAD_1)
                        _mathOperation(ISUB, ILOAD_2, ICONST_1)
                        _ldc("FizzBuzz")
                        _instruction(AASTORE)

                        _goto(32)
                    }
                }

                _stackFrame {
                    _same()
                }

                // else if(i % 3) 
                _mathOperation(IREM, ILOAD_2, ICONST_3)
                _if(IFNE) {
                    _instruction(ALOAD_1)
                    _mathOperation(ISUB, ILOAD_2, ICONST_1)
                    _ldc("Fizz")
                    _instruction(AASTORE)

                    _goto(16)
                }

                _stackFrame {
                    _same()
                }

                // else if(i % 3) 
                _mathOperation(IREM, ILOAD_2, ICONST_5)
                _if(IFNE) {
                    _instruction(ALOAD_1)
                    _mathOperation(ISUB, ILOAD_2, ICONST_1)
                    _ldc("Buzz")
                    _instruction(AASTORE)
                }

                _stackFrame {
                    _same()
                }

                // i++
                _instruction(IINC) {
                    _index(2u)
                    _const(1)
                }

                _goto(forLabel)
            }

            _stackFrame {
                _chop(1)
            }

            _instruction(ALOAD_1)
            _instruction(ARETURN)
        }
    } returns Array<String>::class.java
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