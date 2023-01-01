package com.github.pgreze.kyper

public class KyperExit(
    public val returnCode: Int = 0,
    public val error: String = "",
) : RuntimeException()

public class PrintHelp(
    public val thisCommandOnly: Boolean = true,
    public val returnCode: Int = 1,
) : RuntimeException()
