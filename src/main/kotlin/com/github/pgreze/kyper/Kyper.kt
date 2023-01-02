package com.github.pgreze.kyper

import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect
import kotlin.system.exitProcess

/**
 * Register methods/lambdas in a DSL style.
 * @param help see [Kyper.help]
 * @param init enable a DSL like syntax with [Kyper.register] methods.
 * @return the initialized [Kyper] instance.
 */
public fun kyper(
    help: String? = null,
    init: Kyper.() -> Unit,
): Kyper =
    Kyper(help).apply(init)

/**
 * Convenient method allowing to create a [Kyper] instance
 * provisioned with [Kyper.registerPublicMethods] for the given instance.
 * @param help see [Kyper.help]
 * @return the initialized [Kyper] instance.
 */
public fun Any.kyper(help: String? = null): Kyper =
    Kyper(help).registerPublicMethods(instance = this)

/**
 * Exposes [register] functions to register functions/lambdas as commands,
 * and run them with the [invoke] functions.
 * @see kyper
 * @see Any.kyper
 */
@Suppress("TooManyFunctions", "MagicNumber")
public class Kyper(
    /** The global help message when ran in multi-command mode. */
    internal val help: String? = null,
) {
    internal val commands = mutableMapOf<String, Command>()

    /**
     * Register a Kotlin function as command.
     *
     * Example: **kyper.register(::myFunc)**
     *
     * @see Help
     */
    public fun register(command: KFunction<*>): Kyper = this.also {
        commands[command.name] = Command.Function(command)
    }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: () -> Unit): Kyper = this.also {
        commands[name] = Command.Lambda(name, help, command.reflect()!!) { command() }
    }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: (String) -> Unit): Kyper = this.also {
        commands[name] = Command.Lambda(name, help, command.reflect()!!) { command(it[0]) }
    }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: (String, String) -> Unit): Kyper = this.also {
        commands[name] = Command.Lambda(name, help, command.reflect()!!) { command(it[0], it[1]) }
    }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: (String, String, String) -> Unit): Kyper =
        this.also {
            commands[name] = Command.Lambda(name, help, command.reflect()!!) { command(it[0], it[1], it[2]) }
        }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: (String, String, String, String) -> Unit): Kyper =
        this.also {
            commands[name] = Command.Lambda(name, help, command.reflect()!!) { command(it[0], it[1], it[2], it[3]) }
        }

    public fun unregister(name: String): Boolean =
        commands.remove(name) != null

    /**
     * Uses reflection to register all public methods of the given [instance] as commands.
     *
     * Example: **kyper.registerPublicMethods(MyObject)**
     *
     * Example: **kyper.registerPublicMethods(MyClass())**
     */
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

    @Suppress("UseCheckOrError")
    public operator fun invoke(args: Array<out String>): Unit =
        when {
            commands.isEmpty() ->
                throw IllegalStateException("No command registered")

            args.firstOrNull() in HELP_FLAGS ->
                when {
                    commands.size == 1 ->
                        commands.values.first().showHelp()

                    args.getOrNull(1) in commands ->
                        commands[args[1]]!!.showHelp()

                    else ->
                        commands.values.showHelp(help)
                }

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
