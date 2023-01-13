package com.github.pgreze.kyper

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class HelpsKtTest {

    private val commands = listOf(
        Kommand.Function(::stringAndChoice),
        Kommand.Function(::boolAndFile),
    )

    @Test
    fun `showHelp for Command`() {
        captureStdout { commands.first().showHelp() } shouldBe """
            Usage: stringAndChoice STRING CHOICE

              function usage doc

            Options:
              -h, --help      Show this message and exit

            Arguments:
              STRING           help for string
              CHOICE [OK, NO]  help for choice

        """.trimIndent()
    }

    @Test
    fun `showHelp for Commands`() {
        captureStdout { commands.showHelp(help = "Run multiple commands") } shouldBe """
            Usage: [OPTIONS] COMMAND [ARGS]...

              Run multiple commands

            Options:
              -h, --help      Show this message and exit

            Commands:
              STRINGANDCHOICE  function usage doc
              BOOLANDFILE      function usage help

        """.trimIndent()
    }
}

internal fun captureStdout(block: () -> Unit): String {
    val array = ByteArrayOutputStream()
    val stream = PrintStream(array)
    val oldOut = System.out
    System.setOut(stream)
    try {
        block()
    } finally {
        System.setOut(oldOut)
        stream.close()
    }
    return array.toString()
}
