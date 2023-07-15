package util

import org.assertj.core.error.BasicErrorMessageFactory
import org.assertj.core.error.ErrorMessageFactory

class ShouldBeEnum private constructor(actual: Class<*>, toBeOrNotToBe: Boolean) : 
    BasicErrorMessageFactory(
    "%nExpecting%n  %s%n" + (if (toBeOrNotToBe) "" else " not ") + "to be an enum",
    actual
) {
    companion object {
        /**
         * Creates a new `[ShouldBeEnum]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldBeEnum(actual: Class<*>): ErrorMessageFactory {
            return ShouldBeEnum(actual, true)
        }

        /**
         * Creates a new `[ShouldBeEnum]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldNotBeEnum(actual: Class<*>): ErrorMessageFactory {
            return ShouldBeEnum(actual, false)
        }
    }
}
