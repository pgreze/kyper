package kyper

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas

internal sealed class Command {

    public abstract val name: String

    internal abstract val help: String?

    internal abstract val parameters: List<KParameter>

    public abstract fun call(args: Array<String>)

    internal class Function(
        private val func: KFunction<*>,
        override val name: String = func.name,
        private val receiver: Any? = null,
    ) : Command() {

        override val help: String?
            get() = func.findAnnotation<Help>()?.help

        override val parameters: List<KParameter> =
            if (receiver != null) func.parameters.drop(1) else func.parameters

        override fun call(args: Array<String>) {
            // TODO: support positional arguments
            val typedArgs: Array<Any?> = parameters.zip(args)
                .map { (kPar, arg) -> kPar.type.convert(arg) }
                .toTypedArray()
            if (receiver == null) {
                func.call(*typedArgs)
            } else {
                func.call(receiver, *typedArgs)
            }
        }
    }

    internal class Lambda(
        override val name: String,
        override val help: String? = null,
        reflect: KFunction<Unit>,
        private val wrapper: (Array<String>) -> Unit,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = reflect.parameters
        override fun call(args: Array<String>) = wrapper(args)
    }
}
