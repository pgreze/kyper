package kyper

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

internal sealed class Command {

    public abstract val name: String

    internal abstract val help: String?

    internal abstract val parameters: List<KParameter>

    public abstract fun call(args: Array<String>)

    internal class Function(
        private val func: KFunction<*>,
        private val instance: Any? = null,
        override val name: String = func.name,
    ) : Command() {

        override val help: String?
            get() = func.findAnnotation<Help>()?.help

        override val parameters: List<KParameter> =
            if (instance != null) func.parameters.drop(1) else func.parameters

        override fun call(args: Array<String>) {
            val typedArgs: Array<Any?> = parameters.zip(args)
                .map { (kPar, arg) -> kPar.type.convert(arg) }
                .toTypedArray()
            if (instance == null) {
                func.call(*typedArgs)
            } else {
                func.call(instance, *typedArgs)
            }
        }
    }

    internal class Func0(
        private val func: kyper.Func0,
        override val name: String,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke()
    }

    internal class Func1(
        private val func: kyper.Func1,
        override val name: String,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke(args[0])
    }

    internal class Func2(
        private val func: kyper.Func2,
        override val name: String,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke(args[0], args[1])
    }

    internal class Func3(
        private val func: kyper.Func3,
        override val name: String,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke(args[0], args[1], args[2])
    }

    internal class Func4(
        private val func: kyper.Func4,
        override val name: String,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke(args[0], args[1], args[2], args[3])
    }
}
