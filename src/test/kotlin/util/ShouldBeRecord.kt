package util

import org.assertj.core.error.BasicErrorMessageFactory
import org.assertj.core.error.ErrorMessageFactory

class ShouldBeRecord private constructor(actual: Class<*>, toBeOrNotToBe: Boolean) : 
    BasicErrorMessageFactory(
    "%nExpecting%n  %s%n" + (if (toBeOrNotToBe) "" else " not ") + "to be a record",
    actual
) {
    companion object {
        /**
         * Creates a new `[ShouldBeRecord]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldBeRecord(actual: Class<*>): ErrorMessageFactory {
            return ShouldBeRecord(actual, true)
        }

        /**
         * Creates a new `[ShouldBeRecord]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldNotBeRecord(actual: Class<*>): ErrorMessageFactory {
            return ShouldBeRecord(actual, false)
        }
    }
}
