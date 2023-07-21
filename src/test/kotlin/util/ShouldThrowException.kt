package util

import org.assertj.core.error.BasicErrorMessageFactory
import org.assertj.core.error.ErrorMessageFactory

class ShouldThrowException private constructor(actual: Class<*>, exceptionCls: Class<in Throwable>, toBeOrNotToBe: Boolean) : 
    BasicErrorMessageFactory(
    "%nExpecting%n  %s%n" + (if (toBeOrNotToBe) " to " else " to not ") + "throw exception ${exceptionCls.simpleName}",
    actual
) {
    companion object {
        /**
         * Creates a new `[ShouldThrowException]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldThrow(actual: Class<*>, exceptionCls: Class<in Throwable>): ErrorMessageFactory {
            return ShouldThrowException(actual, exceptionCls, true)
        }
    }
}
