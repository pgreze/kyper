#!/usr/bin/env kotlinc -script

@file:DependsOn("/Users/pgreze/git/pgreze/kyper/kyper/build/libs/kyper.jar")

import kyper.Help
import kyper.kyper

@Help("function help message")
fun hello(
    @Help("the name to greet")
    name: String
) {
    println("hello $name")
}

kyper().invoke(args)
