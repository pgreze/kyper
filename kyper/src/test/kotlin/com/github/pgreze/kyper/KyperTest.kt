package com.github.pgreze.kyper

import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import com.github.pgreze.kyper.KyperKtTest.PublicInternalPrivate
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas

@OptIn(ExperimentalReflectionOnLambdas::class)
class KyperTest {

    @Nested
    inner class Register {
        @Test
        fun `register function`() {
            val kyper = Kyper().register(::stringAndChoice)

            kyper.commands.keys shouldHaveSingleElement "stringAndChoice"
        }

        @Test
        fun `register Function0`() {
            val name = "function0"
            val kyper = Kyper().register(name) { -> }

            kyper.commands.keys shouldHaveSingleElement name
        }

        @Test
        fun `register Function1`() {
            val name = "function1"
            val kyper = Kyper().register(name) { _ -> }

            kyper.commands.keys shouldHaveSingleElement name
        }

        @Test
        fun `register Function2`() {
            val name = "function2"
            val kyper = Kyper().register(name) { _, _ -> }

            kyper.commands.keys shouldHaveSingleElement name
        }

        @Test
        fun `register Function3`() {
            val name = "function3"
            val kyper = Kyper().register(name) { _, _ -> }

            kyper.commands.keys shouldHaveSingleElement name
        }

        @Test
        fun `register Function4`() {
            val name = "function4"
            val kyper = Kyper().register(name) { _, _, _, _ -> }

            kyper.commands.keys shouldHaveSingleElement name
        }
    }

    @Test
    fun unregister() {
        val kyper = Kyper().register(::stringAndChoice)

        kyper.unregister(::stringAndChoice.name)

        kyper.commands shouldBe mapOf()
    }

    @Nested
    inner class Invoke {
        @Test
        fun `invoke empty instance`() {
            assertThrows<IllegalStateException> {
                Kyper().invoke()
            }
        }

        @Test
        fun `invoke single command`() {
            val capture = CaptureInvocations()
            val kyper = Kyper().register(capture::greet)

            kyper.invoke("hello")

            capture.invocations shouldHaveSingleElement listOf("greet", "hello")
        }

        @Test
        fun `invoke many commands`() {
            val capture = CaptureInvocations()
            val kyper = Kyper().registerPublicMethods(capture)

            kyper.invoke("bye", "you")

            capture.invocations shouldHaveSingleElement listOf("bye", "you")
        }

        @Test
        fun `invoke --help with a single command`() {
            val globalHelp = "global help message"
            val kyper = PublicInternalPrivate().kyper(help = globalHelp)

            val stdout = captureStdout { kyper.invoke("--help") }

            stdout shouldContain "public"
            stdout shouldNotContain globalHelp
        }

        @Test
        fun `invoke --help with many commands`() {
            val globalHelp = "global help message"
            val kyper = CaptureInvocations().kyper(help = globalHelp)

            captureStdout { kyper.invoke("--help") } shouldContain globalHelp
        }

        @Test
        fun `invoke --help command`() {
            val globalHelp = "global help message"
            val kyper = CaptureInvocations().kyper(help = globalHelp)

            val stdout = captureStdout { kyper.invoke("--help", "greet") }

            stdout shouldContain "greet"
            stdout shouldNotContain globalHelp
        }

        @Test
        fun `invoke command --help`() {
            val globalHelp = "global help message"
            val kyper = CaptureInvocations().kyper(help = globalHelp)

            val stdout = captureStdout { kyper.invoke("greet", "--help") }

            stdout shouldContain "greet"
            stdout shouldNotContain globalHelp
        }
    }
}
