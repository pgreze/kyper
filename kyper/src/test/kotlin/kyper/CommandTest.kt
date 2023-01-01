package kyper

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class CommandTest {

    @Nested
    inner class Function {
        private val function = Command.Function(::functionWithDefaults)

        @Test
        fun name() {
            function.name shouldBe "functionWithDefaults"
        }

        @Test
        fun help() {
            function.help shouldBe "helpful text"
        }

        @Test
        fun parameters() {
            function.parameters.map(KParameter::toTestParameter) shouldBe listOf(
                TestParameter(index = 0, name = "file", type = typeOf<File>()),
                TestParameter(index = 1, name = "repeat", type = typeOf<Int>(), isOptional = true),
                TestParameter(index = 2, name = "flag", type = typeOf<Boolean>(), isOptional = true),
                TestParameter(index = 3, name = "many", type = typeOf<Array<out String>>(), isVararg = true),
            )
        }

        @Test
        fun call() {
            val args = "myfile 3 true f1 f2 f3".split(" ").toTypedArray()

            val res = function.call(args)

            res.shouldBeTypeOf<Array<Any>> {
                arrayOf(File("myfile"), 3, true, arrayOf("f1", "f2", "f3"))
            }
        }
    }

    @Nested
    inner class Lambda {

    }
}

data class TestParameter(
    val index: Int,
    val name: String?,
    val type: KType,
    val isOptional: Boolean = false,
    val isVararg: Boolean = false,
)

fun KParameter.toTestParameter(): TestParameter =
    TestParameter(
        index = index,
        name = name,
        type = type,
        isOptional = isOptional,
        isVararg = isVararg,
    )
