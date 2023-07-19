package util

import org.assertj.core.api.AbstractClassAssert
import java.lang.reflect.Field

class EnhancedClassAssertions(actual: Class<*>?) :
    AbstractClassAssert<EnhancedClassAssertions>(actual, EnhancedClassAssertions::class.java) {

    fun isEnum(): EnhancedClassAssertions {
        isNotNull()
        assertIsEnum()
        return myself
    }

    fun hasFieldWithType(fieldName: String, fieldClass: Class<*>): EnhancedClassAssertions {
        isNotNull

        actual.declaredFields.find { it.name == fieldName }
            ?: throw assertionError(ShouldHasField.shouldHasField(actual, fieldName, fieldClass))

        return myself
    }

    fun hasConstructor(vararg parameterTypes: Class<*>): EnhancedClassAssertions {
        isNotNull()

        try {
            actual.getConstructor(*parameterTypes)
        } catch (nsme: NoSuchMethodException) {
            throw assertionError(ShouldHasConstructor.shouldHasConstructor(actual, parameterTypes.toList()))
        } catch (se: SecurityException) {
            throw assertionError(ShouldHasConstructor.shouldHasConstructor(actual, parameterTypes.toList()))
        }

        return myself
    }

    fun doesNotHaveConstructor(vararg parameterTypes: Class<*>): EnhancedClassAssertions {
        isNotNull()

        try {
            actual.getConstructor(*parameterTypes)
        } catch (nsme: NoSuchMethodException) {
            return myself
        } catch (se: SecurityException) {
            return myself
        }

        throw assertionError(ShouldHasConstructor.shouldNotHasConstructor(actual, parameterTypes.toList()))
    }

    fun implementsInterface(interfaceCls: Class<*>): EnhancedClassAssertions {
        isNotNull()

        val implements = actual.interfaces.contains(interfaceCls)
        if (!implements) {
            throw assertionError(ShouldImplementInterface.shouldImplement(actual, interfaceCls))
        }

        return myself
    }

    fun doesNotImplementInterface(interfaceCls: Class<*>): EnhancedClassAssertions {
        isNotNull()

        val implements = actual.interfaces.contains(interfaceCls)
        if (implements) {
            throw assertionError(ShouldImplementInterface.shouldNotImplement(actual, interfaceCls))
        }

        return myself
    }

    private fun assertIsEnum() {
        if (!actual.isEnum) {
            throw assertionError(ShouldBeEnum.shouldBeEnum(actual))
        }
    }

    private fun assertIsNotEnum() {
        if (!actual.isEnum) {
            throw assertionError(ShouldBeEnum.shouldNotBeEnum(actual))
        }
    }
}