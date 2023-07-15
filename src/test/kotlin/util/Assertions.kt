package util

fun assertThat(cls: Class<*>) : EnhancedClassAssertions {
    return EnhancedClassAssertions(cls)
}