package kyper

import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect
import kotlin.system.exitProcess

/**
 * Convenient method allowing to create a [Kyper] instance
 * provisioned with [Kyper.registerPublicMethods] for the given instance.
 */
public fun Any.kyper(): Kyper =
    Kyper().registerPublicMethods(instance = this)

/**
 * @param init enable a DSL like syntax with [Kyper.register] methods.
 */
public fun kyper(init: Kyper.() -> Unit): Kyper =
    Kyper().apply(init)

public class Kyper {

    private val commands = mutableMapOf<String, Command>()

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
    public fun register(name: String, help: String? = null, command: (String, String, String) -> Unit): Kyper = this.also {
        commands[name] = Command.Lambda(name, help, command.reflect()!!) { command(it[0], it[1], it[2]) }
    }

    @ExperimentalReflectionOnLambdas
    public fun register(name: String, help: String? = null, command: (String, String, String, String) -> Unit): Kyper =
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

            args.firstOrNull() in arrayOf("help", "--help", "-h") ->
                showHelp(commands)

            // Invalid or missing command name
            commands.size > 1 && args.firstOrNull() !in commands -> {
                showHelp(commands)
                exitProcess(1)
            }

            else -> {
                val command = if (commands.size == 1) {
                    commands.values.first()
                } else {
                    commands[args.first()]!!
                }

                try {
                    command.call(args.drop(1).toTypedArray())
                } catch (e: PrintHelp) {
                    showHelp(commands.filter { e.thisCommandOnly.not() || it.key == command.name })
                    exitProcess(e.returnCode)
                } catch (e: KyperExit) {
                    if (e.error.isNotEmpty()) {
                        System.err.println(e.error)
                    }
                    exitProcess(e.returnCode)
                }
            }
        }

    private fun showHelp(commands: Map<String, Command>) {
        println("Usage:")
        // TODO: add help --help -h
        commands.forEach { (name, command) ->
            println(">> $name: ${command.help ?: ""}")
            command.parameters.forEach {
                println("${it.name} ${it.findAnnotation<Help>()?.help ?: ""}")
            }
        }
    }
}
