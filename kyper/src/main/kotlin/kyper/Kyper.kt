package kyper

import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.system.exitProcess

/**
 * Convenient method allowing to create a [Kyper] instance
 * provisioned with [Kyper.registerPublicMethods] for the given instance.
 */
public fun Any.kyper(): Kyper =
    Kyper().registerPublicMethods(instance = this)

public class Kyper {

    private val commands = mutableMapOf<String, Command>()

    public fun register(command: KFunction<*>): Kyper = this.also {
        commands[command.name] = Command.Function(command)
    }

    public fun registerPublicMethods(instance: Any): Kyper = this.also {
        val defaultMethods = arrayOf("equals", "hashCode", "toString")
        // TODO: try instance::class.java.methods
        instance::class.members.asSequence()
            .filterIsInstance<KFunction<*>>()
            .filter { it.name !in defaultMethods }
            .filter { it.visibility == KVisibility.PUBLIC }
            .forEach { commands[it.name] = Command.Function(it, instance) }
    }

    public fun register(name: String, help: String? = null, command: Func0): Kyper = this.also {
        commands[name] = Command.Func0(command, help)
    }

    public fun register(name: String, help: String? = null, command: Func1): Kyper = this.also {
        commands[name] = Command.Func1(command, help)
    }

    public fun register(name: String, help: String? = null, command: Func2): Kyper = this.also {
        commands[name] = Command.Func2(command, help)
    }

    public fun register(name: String, help: String? = null, command: Func3): Kyper = this.also {
        commands[name] = Command.Func3(command, help)
    }

    public fun register(name: String, help: String? = null, command: Func4): Kyper = this.also {
        commands[name] = Command.Func4(command, help)
    }

    public fun unregister(name: String): Boolean =
        commands.remove(name) != null

    @JvmName("invokeWith")
    public operator fun invoke(vararg args: String) {
        invoke(args)
    }

    public operator fun invoke(args: Array<out String>) {
        when (val command = args.firstOrNull()) {
            in commands -> {
                try {
                    commands[command]!!.call(args.drop(1).toTypedArray())
                } catch (e: PrintHelp) {
                    showHelp(commands.filter { e.thisCommandOnly.not() || it.key == command })
                    exitProcess(e.returnCode)
                } catch (e: KyperExit) {
                    if (e.error.isNotEmpty()) {
                        System.err.println(e.error)
                    }
                    exitProcess(e.returnCode)
                }
            }

            "help", "--help", "-h" ->
                showHelp(commands)

            else -> {
                showHelp(commands)
                exitProcess(1)
            }
        }
    }
}

private fun showHelp(commands: Map<String, Command>) {
    println("\n\ncommands:")
    // TODO: add help --help -h
    commands.forEach { (name, command) ->
        println(">> $name: ${command.help ?: ""}")
        command.parameters.forEach {
            println("${it.name} ${it.findAnnotation<Help>()?.help ?: ""}")
        }
    }
}
