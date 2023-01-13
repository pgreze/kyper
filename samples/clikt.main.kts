@file:DependsOn("com.github.ajalt.clikt:clikt-jvm:3.5.1")

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int

class Time : CliktCommand() {
    override fun run() {
        echo(System.currentTimeMillis())
    }
}

class Hello : CliktCommand() {
    val name: String by argument(help="The person to greet")
    val count: Int by option(help="Number of greetings")
        .int()
        .default(1)
    val mode: Boolean by option().flag()
    val lang: String by option(help="Number of greetings")
        .choice("en", "fr")
        .default("en")

    override fun run() {
        repeat(count) {
            echo("Hello $name!")
        }
    }
}

Hello().main(args)
