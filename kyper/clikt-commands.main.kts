@file:DependsOn("com.github.ajalt.clikt:clikt-jvm:3.5.1")

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int

class Root : NoOpCliktCommand(help = "Run several commands")

class Tool : CliktCommand(help = "A tool that runs") {
    val verbose by option().flag("--no-verbose")
    override fun run() = Unit
}

class Execute : CliktCommand(help = "Execute the command") {
    val name by option()
    override fun run() = Unit
}

Root().subcommands(Tool(), Execute()).main(args)
