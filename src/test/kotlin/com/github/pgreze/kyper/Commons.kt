@file:Suppress("UNUSED_PARAMETER", "unused", "UnusedPrivateMember")

package com.github.pgreze.kyper

import java.io.File

enum class Choice { OK, NO }

@Help("function usage doc")
fun stringAndChoice(
    @Help("help for string")
    string: String,
    @Help("help for choice")
    choice: Choice,
) { println("stringAndChoice") }

@Help("function usage help")
fun boolAndFile(
    @Help("help for bool")
    bool: Boolean,
    @Help("help for file")
    file: File,
) { println("boolAndFile") }

@Help("helpful text")
fun functionWithDefaults(
    @Help("help for file")
    file: File,
    @Help("help for repeat")
    repeat: Int = 2,
    @Help("help for flag")
    flag: Boolean = false,
    @Help("help for many")
    vararg many: String,
): Array<Any> = // Notice List is converted to Array when returned by
    arrayOf(file, repeat, flag, many.copyOf())

class PublicInternalPrivate {
    public fun public() { println("public") }

    internal fun internal() { println("internal") }

    private fun private() { println("private") }
}

class CaptureInvocations {
    val invocations = mutableListOf<List<String>>()

    fun greet(name: String) {
        invocations.add(listOf("greet", name))
    }

    fun bye(name: String) {
        invocations.add(listOf("bye", name))
    }
}
