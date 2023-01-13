#!/usr/bin/env kotlinc -script

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("com.github.pgreze:kyper:0.3")

import com.github.pgreze.kyper.Command
import com.github.pgreze.kyper.kyper
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.file.Path

@Command
fun main(
//    string: String = "arg",
//    int: Int = 1,
//    float: Float = 1.2f,
//    double: Double = 3.14,
//    long: Long = Long.MAX_VALUE,
//    boolean: Boolean = true,
//    bigInteger: BigInteger = BigInteger.valueOf(12),
//    bigDecimal: BigDecimal = BigDecimal.valueOf(12.3),
//    file: File = File("file"),
//    path: Path = Path.of("path"),
    choice: Choice = Choice.NO,
    vararg strings: String, // Only String/File vararg are supported.
) {
    println(strings.toList())
}

enum class Choice { YES, NO }

kyper().invoke(args)
