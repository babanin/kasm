package katartal.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class UtilsKtTest {

    @ParameterizedTest(name = "Descriptor of {0} should be {1}")
    @MethodSource("shouldConvertClassToDescriptor")
    fun shouldConvertClassToDescriptor(cls: Class<*>, expected: String) {
        // expect
        Assertions.assertThat(cls.descriptor())
            .isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun shouldConvertClassToDescriptor(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Boolean::class.java,                               "Z"),
                Arguments.of(Byte::class.java,                                  "B"),
                Arguments.of(Char::class.java,                                  "C"),
                Arguments.of(Double::class.java,                                "D"),
                Arguments.of(Float::class.java,                                 "F"),
                Arguments.of(Int::class.java,                                   "I"),
                Arguments.of(Long::class.java,                                  "J"),
                Arguments.of(Void::class.java,                                  "V"),
                Arguments.of(java.lang.Boolean::class.java,                     "Ljava/lang/Boolean;"),
                Arguments.of(java.lang.Byte::class.java,                        "Ljava/lang/Byte;"),
                Arguments.of(java.lang.Character::class.java,                   "Ljava/lang/Character;"),
                Arguments.of(java.lang.Double::class.java,                      "Ljava/lang/Double;"),
                Arguments.of(java.lang.Float::class.java,                       "Ljava/lang/Float;"),
                Arguments.of(java.lang.Integer::class.java,                     "Ljava/lang/Integer;"),
                Arguments.of(java.lang.Long::class.java,                        "Ljava/lang/Long;"),
                Arguments.of(BooleanArray::class.java,                          "[Z"),
                Arguments.of(ByteArray::class.java,                             "[B"),
                Arguments.of(CharArray::class.java,                             "[C"),
                Arguments.of(DoubleArray::class.java,                           "[D"),
                Arguments.of(FloatArray::class.java,                            "[F"),
                Arguments.of(IntArray::class.java,                              "[I"),
                Arguments.of(LongArray::class.java,                             "[J"),
                Arguments.of(Array<BooleanArray>::class.java,                   "[[Z"),
                Arguments.of(Array<ByteArray>::class.java,                      "[[B"),
                Arguments.of(Array<CharArray>::class.java,                      "[[C"),
                Arguments.of(Array<DoubleArray>::class.java,                    "[[D"),
                Arguments.of(Array<FloatArray>::class.java,                     "[[F"),
                Arguments.of(Array<IntArray>::class.java,                       "[[I"),
                Arguments.of(Array<LongArray>::class.java,                      "[[J"),
                Arguments.of(Array<Array<BooleanArray>>::class.java,            "[[[Z"),
                Arguments.of(Array<Array<Array<BooleanArray>>>::class.java,     "[[[[Z"),
                Arguments.of(Array<String>::class.java,                         "[Ljava/lang/String;"),
                Arguments.of(Array<Array<String>>::class.java,                  "[[Ljava/lang/String;"),
            )
        }
    }

}