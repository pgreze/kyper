package com.github.pgreze.kyper

import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

@Suppress("CyclomaticComplexMethod")
internal fun KType.convert(args: Array<out String>, index: Int): Any =
    when (this) {
        typeOf<String>() ->
            args[index]

        typeOf<Int>() ->
            args[index].toInt()

        typeOf<Float>() ->
            args[index].toFloat()

        typeOf<Double>() ->
            args[index].toDouble()

        typeOf<Long>() ->
            args[index].toLong()

        typeOf<Boolean>() ->
            args[index].toBoolean()

        typeOf<BigInteger>() ->
            args[index].toBigInteger()

        typeOf<BigDecimal>() ->
            args[index].toBigDecimal()

        typeOf<File>() ->
            File(args[index])

        typeOf<Path>() ->
            Path.of(args[index])

        else -> when {
            isSubtypeOf(typeOf<Enum<*>>()) ->
                enumValueOf(args[index])

            isSubtypeOf(typeOf<Array<out String>>()) -> // For vararg parameters, type is Array<out ??>
                args.drop(index).toOutArray()

            isSubtypeOf(typeOf<Array<out File>>()) -> // For vararg parameters, type is Array<out ??>
                args.drop(index).map(::File).toOutArray()

            isSubtypeOf(typeOf<Array<out Path>>()) -> // For vararg parameters, type is Array<out ??>
                args.drop(index).map(Path::of).toOutArray()

            else ->
                throw IllegalArgumentException("Unsupported $this")
        }
    }

// https://stackoverflow.com/a/46422600/5489877
@Suppress("UNCHECKED_CAST")
internal fun KType.enumValues(): Array<out Enum<*>> =
    (classifier as KClass<Enum<*>>).java.enumConstants as Array<out Enum<*>>

private fun KType.enumValueOf(value: String): Enum<*> =
    enumValues().first { it.name == value }

private fun List<String>.toOutArray(): Array<out String> =
    toTypedArray()

private fun List<File>.toOutArray(): Array<out File> =
    toTypedArray()

private fun List<Path>.toOutArray(): Array<out Path> =
    toTypedArray()
