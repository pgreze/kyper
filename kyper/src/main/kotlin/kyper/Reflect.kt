package kyper

import java.io.File
import java.nio.file.Path
import kotlin.reflect.KType
import kotlin.reflect.typeOf

internal fun KType.convert(arg: String): Any =
    when (this) {
        typeOf<Int>() -> arg.toInt()
        typeOf<Float>() -> arg.toFloat()
        typeOf<File>() -> File(arg)
        typeOf<Path>() -> Path.of(arg)
        else -> arg
    }
