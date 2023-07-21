package util

import org.assertj.core.api.*

object Assertions {
    @JvmStatic
    fun assertThat(cls: Class<*>): EnhancedClassAssertions {
        return EnhancedClassAssertions(cls)
    }

    fun <T> assertThat(actual: Array<T>): ObjectArrayAssert<T> {
        return AssertionsForClassTypes.assertThat(actual)
    }

    fun <ELEMENT> assertThat(actual: List<ELEMENT>?): ListAssert<ELEMENT> {
        return AssertionsForInterfaceTypes.assertThat(actual)
    }

    fun assertThat(actual: IntArray): AbstractIntArrayAssert<*> {
        return AssertionsForClassTypes.assertThat(actual)
    }

    fun assertThat(actual: Boolean): AbstractBooleanAssert<*> {
        return AssertionsForClassTypes.assertThat(actual)
    }
}