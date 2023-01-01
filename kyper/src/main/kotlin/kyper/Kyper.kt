package kyper

import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect
import kotlin.system.exitProcess

/**
 * Convenient method allowing to create a [Kyper] instance
 * provisioned with [Kyper.registerPublicMethods] for the given instance.
 */
public fun Any.kyper(help: String? = null): Kyper =
    Kyper(help).registerPublicMethods(instance = this)

/**
 * @param init enable a DSL like syntax with [Kyper.register] methods.
 */
public fun kyper(
    help: String? = null,
    init: Kyper.() -> Unit,
): Kyper =
    Kyper(help).apply(init)

public class Kyper(
    private val help: String? = null,
) {
    private val commands = mutableMapOf<String, Command>()

    public fun register(command: KFunction<*>): Kyper = this.also {
        commands[command.name] = Command.Function(command)
    }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: () -> Any?): Kyper = this.also {
        commands[name] = Command.Lambda(name, help, command.reflect()!!) { command() }
    }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: (String) -> Any?): Kyper = this.also {
        commands[name] = Command.Lambda(name, help, command.reflect()!!) { command(it[0]) }
    }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: (String, String) -> Any?): Kyper = this.also {
        commands[name] = Command.Lambda(name, help, command.reflect()!!) { command(it[0], it[1]) }
    }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: (String, String, String) -> Any?): Kyper =
        this.also {
            commands[name] = Command.Lambda(name, help, command.reflect()!!) { command(it[0], it[1], it[2]) }
        }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: (String, String, String, String) -> Any?): Kyper =
        this.also {
            commands[name] = Command.Lambda(name, help, command.reflect()!!) { command(it[0], it[1], it[2], it[3]) }
        }

    public fun unregister(name: String): Boolean =
        commands.remove(name) != null

    public fun registerPublicMethods(instance: Any): Kyper = this.also {
        val defaultMethods = arrayOf("equals", "hashCode", "toString")
        instance::class.members.asSequence()
            .filterIsInstance<KFunction<*>>()
            .filter { it.name !in defaultMethods }
            .filter { it.visibility == KVisibility.PUBLIC }
            .forEach { commands[it.name] = Command.Function(it, receiver = instance) }
    }

    @JvmName("invokeWith")
    public operator fun invoke(vararg args: String) {
        invoke(args)
    }

    public operator fun invoke(args: Array<out String>): Unit =
        when {
            commands.isEmpty() -> {
                System.err.println("No command registered")
                exitProcess(1)
            }

            args.firstOrNull() in HELP_FLAGS ->
                args.getOrNull(1)
                    .let(commands::get)
                    ?.showHelp()
                    ?: commands.values.showHelp(help)

            // Invalid or missing command name
            commands.size > 1 && args.firstOrNull() !in commands -> {
                commands.values.showHelp(help)
                exitProcess(1)
            }

            else -> {
                val (command, args) = if (commands.size == 1) {
                    commands.values.first() to args
                } else {
                    commands[args.first()]!! to args.drop(1).toTypedArray()
                }

                if (args.firstOrNull() in HELP_FLAGS) {
                    command.showHelp()
                } else {
                    call(command, args)
                }
            }
        }

    private fun call(command: Command, args: Array<out String>) {
        try {
            command.call(args)
        } catch (e: PrintHelp) {
            if (e.thisCommandOnly) {
                command.showHelp()
            } else {
                commands.values.showHelp(help)
            }
            exitProcess(e.returnCode)
        } catch (e: KyperExit) {
            if (e.error.isNotEmpty()) {
                System.err.println(e.error)
            }
            exitProcess(e.returnCode)
        }
    }
}
