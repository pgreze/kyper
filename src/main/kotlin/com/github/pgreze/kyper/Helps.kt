package com.github.pgreze.kyper

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

internal val HELP_FLAGS = arrayOf("-h", "--help")

internal fun Collection<Kommand>.showHelp(help: String?) {
    println("Usage: [OPTIONS] COMMAND [ARGS]...")
    println()
    help?.let {
        println("  $it")
        println()
    }
    println("Options:")
    println("  ${HELP_FLAGS.joinToString(", ")}      Show this message and exit")
    println()
    printSection("Commands") {
        it.name.uppercase() to it.help
    }
}

internal fun Kommand.showHelp() {
    val params = parameters.joinToString(" ") {
        val name = it.name!!.uppercase()
        if (it.isOptional) "[$name]" else name
    }
    println("Usage: $name $params")
    println()
    help?.let {
        println("  $it")
        println()
    }
    println("Options:")
    println("  ${HELP_FLAGS.joinToString(", ")}      Show this message and exit")
    println()

    parameters.printSection("Arguments") {
        val name = it.name!!.uppercase()

        val choices = when {
            it.type.isSubtypeOf(typeOf<Enum<*>>()) ->
                it.type.enumValues().map { v -> v.name }
            it.type == typeOf<Boolean>() ->
                listOf("true", "false")
            else -> null
        }?.joinToString(prefix = " [", postfix = "]") ?: ""

        val parameter = it.findAnnotation<Parameter>()

        name + choices to parameter?.help
    }
}

private inline fun <T> Collection<T>.printSection(title: String, block: (T) -> Pair<String, String?>) {
    if (isEmpty()) return
    println("$title:")
    val columns = map(block)
    val maxFirstColumnLength = columns.maxOf { it.first.length }
    columns.forEach { (name, help) ->
        if (help.isNullOrBlank()) {
            println("  $name")
        } else {
            println("  ${name.padEnd(maxFirstColumnLength)}  $help")
        }
    }
}
