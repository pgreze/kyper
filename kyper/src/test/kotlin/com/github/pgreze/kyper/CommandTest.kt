package com.github.pgreze.kyper

import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect
import kotlin.reflect.typeOf

class CommandTest {

    @Nested
    inner class Function {

        private val command = Command.Function(::functionWithDefaults)

        @Test
        fun name() {
            command.name shouldBe "functionWithDefaults"
        }

        @Test
        fun help() {
            command.help shouldBe "helpful text"
        }

        @Test
        fun parameters() {
            command.parameters.map(KParameter::toTestParameter) shouldBe listOf(
                TestParameter(index = 0, name = "file", type = typeOf<File>()),
                TestParameter(index = 1, name = "repeat", type = typeOf<Int>(), isOptional = true),
                TestParameter(index = 2, name = "flag", type = typeOf<Boolean>(), isOptional = true),
                TestParameter(index = 3, name = "many", type = typeOf<Array<out String>>(), isVararg = true),
            )
        }

        @Test
        fun call() {
            val args = "myfile 3 true f1 f2 f3".split(" ").toTypedArray()

            val res = command.call(args)

            res.shouldBeTypeOf<Array<Any>> {
                arrayOf(File("myfile"), 3, true, arrayOf("f1", "f2", "f3"))
            }
        }
    }

    @OptIn(ExperimentalReflectionOnLambdas::class)
    @Nested
    inner class Lambda {

        private val calls = mutableListOf<List<String>>()
        private val lambda: (String, String) -> Unit =
            fun(s1: String, s2: String) { calls.add(listOf(s1, s2)) }

        private val command = Command.Lambda(
            name = "lambda name",
            help = "the help",
            reflect = lambda.reflect()!!,
            wrapper = { lambda(it[0], it[1]) },
        )

        @Test
        fun name() {
            command.name shouldBe "lambda name"
        }

        @Test
        fun help() {
            command.help shouldBe "the help"
        }

        @Test
        fun parameters() {
            command.parameters.map(KParameter::toTestParameter) shouldBe listOf(
                TestParameter(index = 0, name = "s1", type = typeOf<String>()),
                TestParameter(index = 1, name = "s2", type = typeOf<String>()),
            )
        }

        @Test
        fun call() {
            val args = listOf("hello", "world")

            command.call(args.toTypedArray())

            calls shouldHaveSingleElement args
        }
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
