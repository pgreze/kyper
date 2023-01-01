// TODO: make generic
@file:DependsOn("/Users/pgreze/git/pgreze/kyper/kyper/build/libs/kyper.jar")
// @file:DependsOn("com.github.pgreze:kyper:WIP")

import kyper.Help
import kyper.kyper

@Help("function help message")
fun hello(name: String) {
    println("hello $name")
}

kyper().invoke(args)
