package katartal.model.method

import katartal.model.method.MethodAccess.Companion.FINAL
import katartal.model.method.MethodAccess.Companion.PUBLIC
import katartal.model.method.MethodAccess.Companion.STATIC
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MethodAccessTest {
    @Test
    fun shouldClearBitsUsingMinusOperator() {
        // given
        val methodAccess = PUBLIC + STATIC + FINAL

        // when
        val result = methodAccess - STATIC

        // then
        assertThat(result[PUBLIC]).isTrue()
        assertThat(result[FINAL]).isTrue()
        assertThat(result[STATIC]).isFalse()
    }
    
    @Test
    fun shouldSetBitsUsingPlusOperator() {
        // given
        val methodAccess = PUBLIC + FINAL

        // when
        val result = methodAccess + STATIC

        // then
        assertThat(result[PUBLIC]).isTrue()
        assertThat(result[FINAL]).isTrue()
        assertThat(result[STATIC]).isTrue()
    }
}