package util

import org.assertj.core.error.BasicErrorMessageFactory
import org.assertj.core.error.ErrorMessageFactory

class ShouldImplementInterface private constructor(actual: Class<*>, interfaceCls: Class<*>, toBeOrNotToBe: Boolean) : 
    BasicErrorMessageFactory(
    "%nExpecting%n  %s%n" + (if (toBeOrNotToBe) " to " else " to not ") + "implement interface ${interfaceCls.simpleName}",
    actual
) {
    companion object {
        /**
         * Creates a new `[ShouldImplementInterface]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldImplement(actual: Class<*>, interfaceCls: Class<*>): ErrorMessageFactory {
            return ShouldImplementInterface(actual, interfaceCls, true)
        }

        /**
         * Creates a new `[ShouldImplementInterface]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldNotImplement(actual: Class<*>, interfaceCls: Class<*>): ErrorMessageFactory {
            return ShouldImplementInterface(actual, interfaceCls, false)
        }
    }
}
