package kyper

import java.io.File
import java.io.Serializable

enum class Choice { OK, NO }

@Help("function usage doc")
fun stringAndChoice(
    @Help("help for string")
    string: String,
    @Help("help for choice")
    choice: Choice,
) {}

@Help("function usage help")
fun boolAndFile(
    @Help("help for bool")
    bool: Boolean,
    @Help("help for file")
    file: File,
) {}

@Help("helpful text")
fun functionWithDefaults(
    @Help("help for file")
    file: File,
    @Help("help for repeat")
    repeat: Int = 2,
    @Help("help for flag")
    flag: Boolean = false,
    @Help("help for many")
    vararg many: String,
): Array<Any> = // Notice List is converted to Array when returned by
    arrayOf(file, repeat, flag, many.copyOf())
