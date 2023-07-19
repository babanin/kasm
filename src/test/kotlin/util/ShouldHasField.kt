package util

import org.assertj.core.error.BasicErrorMessageFactory
import org.assertj.core.error.ErrorMessageFactory

class ShouldHasField private constructor(actual: Class<*>, fieldName : String, fieldClass : Class<*>) : 
    BasicErrorMessageFactory(
    "%nExpecting%n  %s%n" + "to have following field $fieldName with type ${fieldClass.simpleName}",
    actual
) {
    companion object {
        /**
         * Creates a new `[ShouldHasField]`.
         *
         * @param actual the actual value in the failed assertion.
         * @return the created `ErrorMessageFactory`.
         */
        fun shouldHasField(actual: Class<*>, fieldName : String, fieldClass : Class<*>): ErrorMessageFactory {
            return ShouldHasField(actual, fieldName, fieldClass)
        }
    }
}
