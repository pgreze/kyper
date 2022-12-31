package kyper

import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

internal fun KType.convert(arg: String): Any =
    when {
        this == typeOf<String>() -> arg
        this == typeOf<Int>() -> arg.toInt()
        this == typeOf<Float>() -> arg.toFloat()
        this == typeOf<Double>() -> arg.toDouble()
        this == typeOf<Long>() -> arg.toLong()
        this == typeOf<Boolean>() -> arg.toBoolean()
        this == typeOf<BigInteger>() -> arg.toBigInteger()
        this == typeOf<BigDecimal>() -> arg.toBigDecimal()
        this == typeOf<File>() -> File(arg)
        this == typeOf<Path>() -> Path.of(arg)
        isSubtypeOf(typeOf<Enum<*>>()) -> enumValueOf(arg)
        else -> throw RuntimeException("Unsupported $this")
    }

// https://stackoverflow.com/a/46422600/5489877
@Suppress("UNCHECKED_CAST")
internal fun KType.enumValues(): Array<out Enum<*>> =
    (classifier as KClass<Enum<*>>).java.enumConstants as Array<out Enum<*>>

private fun KType.enumValueOf(value: String): Enum<*> =
    enumValues().first { it.name == value }
