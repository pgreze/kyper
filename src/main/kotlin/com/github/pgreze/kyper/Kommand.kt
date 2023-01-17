package com.github.pgreze.kyper

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas

internal sealed class Kommand {

    internal abstract val name: String

    internal abstract val help: String?

    internal abstract val parameters: List<KParameter>

    public abstract fun call(args: Array<out String>): Any?

    internal class Function(
        private val func: KFunction<*>,
        override val name: String = func.name,
        private val receiver: Any? = null,
    ) : Kommand() {

        override val help: String?
            get() = func.findAnnotation<Command>()?.help

        override val parameters: List<KParameter> =
            if (receiver != null) func.parameters.drop(1) else func.parameters

        override fun call(args: Array<out String>): Any? {
            // TODO: support positional arguments
            val paramToValue = mutableMapOf<KParameter, Any>()
            parameters.withIndex()
                .filter { it.index < args.size }
                .forEach { (index, param) -> paramToValue[param] = param.type.convert(args, index) }
            return if (receiver == null) {
                func.callBy(paramToValue)
            } else {
                func.callBy(mapOf(func.parameters.first() to receiver) + paramToValue)
            }
        }
    }

    internal class Lambda(
        override val name: String,
        override val help: String? = null,
        reflect: KFunction<Unit>,
        private val wrapper: (Array<out String>) -> Any?,
    ) : Kommand() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = reflect.parameters
        override fun call(args: Array<out String>): Any? = wrapper(args)
    }
}
