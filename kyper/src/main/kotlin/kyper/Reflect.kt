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

internal typealias Func0 = () -> Unit
internal typealias Func1 = (String) -> Unit
internal typealias Func2 = (String, String) -> Unit
internal typealias Func3 = (String, String, String) -> Unit
internal typealias Func4 = (String, String, String, String) -> Unit
