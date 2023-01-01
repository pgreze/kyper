// TODO: make generic
@file:DependsOn("/Users/pgreze/git/pgreze/kyper/kyper/build/libs/kyper.jar")
// @file:DependsOn("com.github.pgreze:kyper:WIP")

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
