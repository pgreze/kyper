#!/usr/bin/env kotlinc -script

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("com.github.pgreze:kyper:0.3")

import com.github.pgreze.kyper.Command
import com.github.pgreze.kyper.kyper
import java.io.File

@Command
fun main(
    choice: Choice = Choice.NO,
    optionalArg: File? = null,
) {
    println(optionalArg?.absoluteFile)
}

enum class Choice { YES, NO }

kyper().invoke(args)
