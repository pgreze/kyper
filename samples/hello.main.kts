#!/usr/bin/env kotlinc -script

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("com.github.pgreze:kyper:0.3")

import com.github.pgreze.kyper.Command
import com.github.pgreze.kyper.Parameter
import com.github.pgreze.kyper.kyper

@Command("function help message")
fun hello(
    @Parameter("the name to greet")
    name: String
) {
    println("hello $name")
}

kyper().invoke(args)
