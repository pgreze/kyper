@file:Suppress("UNUSED_PARAMETER", "unused", "UnusedPrivateMember")

package com.github.pgreze.kyper

import java.io.File

enum class Choice { OK, NO }

@Command("function usage doc")
fun stringAndChoice(
    @Parameter("help for string")
    string: String,
    @Parameter("help for choice")
    choice: Choice,
) { println("stringAndChoice") }

@Command("function usage help")
fun boolAndFile(
    @Parameter("help for bool")
    bool: Boolean,
    @Parameter("help for file")
    file: File,
) { println("boolAndFile") }

@Command("helpful text")
fun functionWithDefaults(
    @Parameter("help for file")
    file: File,
    @Parameter("help for repeat")
    repeat: Int = 2,
    @Parameter("help for flag")
    flag: Boolean = false,
    @Parameter("help for many")
    vararg many: String,
): Array<Any> = // Notice List is converted to Array when returned by
    arrayOf(file, repeat, flag, many.copyOf())

class SingleCommand {
    @Command
    public operator fun invoke() { println("bonjour") }
}

class CaptureInvocations {
    val invocations = mutableListOf<List<String>>()

    @Command
    fun greet(name: String) {
        invocations.add(listOf("greet", name))
    }

    @Command
    fun bye(name: String) {
        invocations.add(listOf("bye", name))
    }
}
