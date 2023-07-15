package util

import org.assertj.core.error.BasicErrorMessageFactory
import org.assertj.core.error.ErrorMessageFactory

class ShouldHasConstructor private constructor(actual: Class<*>, parameters: List<Class<*>>, toBeOrNotToBe: Boolean) :
    BasicErrorMessageFactory(
        "%nExpecting%n  %s%n" + (if (toBeOrNotToBe) "" else " not ") + "to have constructor with parameters ${
            parameters.joinToString { it.simpleName }
        }}",
        actual
    ) {
    companion object {
        /**
         * Creates a new `[ShouldHasConstructor]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldHasConstructor(actual: Class<*>, parameters: List<Class<*>>): ErrorMessageFactory {
            return ShouldHasConstructor(actual, parameters, true)
        }

        /**
         * Creates a new `[ShouldHasConstructor]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldNotHasConstructor(actual: Class<*>, parameters: List<Class<*>>): ErrorMessageFactory {
            return ShouldHasConstructor(actual, parameters, false)
        }
    }
}
