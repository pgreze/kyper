package kyper

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.typeOf

class TypesKtTest {

    data class MyClass(
        val string: String = "arg",
        val int: Int = 1,
        val float: Float = 1.2f,
        val double: Double = 3.14,
        val long: Long = Long.MAX_VALUE,
        val boolean: Boolean = true,
        val bigInteger: BigInteger = BigInteger.valueOf(12),
        val bigDecimal: BigDecimal = BigDecimal.valueOf(12.3),
        val file: File = File("file"),
        val path: Path = Path.of("path"),
        val choice: Choice = Choice.NO,
    )

    companion object {
        private val kFunction = MyClass::class.primaryConstructor!!
        private val instance = kFunction.callBy(mapOf())

        @JvmStatic
        private fun typeToValueProvider(): Stream<Arguments> =
            MyClass::class.declaredMemberProperties
                .also { require(it.size == 11) { "Invalid resolution of properties=$it" } }
                .map { Arguments.of(it.returnType, it.javaGetter!!.invoke(instance)) }
                .stream()
    }

    @ParameterizedTest
    @MethodSource("typeToValueProvider")
    fun convert(kType: KType, value: Any) {
        kType.convert(value.toString()) shouldBe value
    }

    @Test
    fun `convert unsupported type`() {
        val kType = typeOf<StringBuilder>()

        assertThrows<RuntimeException> { kType.convert("") }
            .message shouldContain "$kType"
    }

    @Test
    fun enumValues() {
        typeOf<Choice>().enumValues() shouldBe Choice.values()
    }
}
