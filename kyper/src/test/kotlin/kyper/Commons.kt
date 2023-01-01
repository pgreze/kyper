package kyper

import java.io.File

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
