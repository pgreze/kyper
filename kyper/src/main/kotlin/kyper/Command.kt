package kyper

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

internal sealed class Command {

    abstract val help: String?

    abstract val parameters: List<KParameter>

    abstract fun call(args: Array<String>)

    class Function(
        private val func: KFunction<*>,
        private val instance: Any? = null,
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

    class Func0(
        private val func: kyper.Func0,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke()
    }

    class Func1(
        private val func: kyper.Func1,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke(args[0])
    }

    class Func2(
        private val func: kyper.Func2,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke(args[0], args[1])
    }

    class Func3(
        private val func: kyper.Func3,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke(args[0], args[1], args[2])
    }

    class Func4(
        private val func: kyper.Func4,
        override val help: String? = null,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = func.reflect()!!.parameters
        override fun call(args: Array<String>) = func.invoke(args[0], args[1], args[2], args[3])
    }
}
