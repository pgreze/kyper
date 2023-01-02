package com.github.pgreze.kyper

/** Exits the given command. */
public class KyperExit(
    public val returnCode: Int = 0,
    /** Print this message in stderr if not empty. */
    public val error: String = "",
) : RuntimeException()

/** Triggers the help display and exit. */
public class PrintHelp(
    /** Display the current command help by default, or all commands if false. */
    public val thisCommandOnly: Boolean = true,
    /** Exit status, default being an error. */
    public val returnCode: Int = 1,
) : RuntimeException()
