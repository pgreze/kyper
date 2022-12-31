// TODO: make generic
@file:DependsOn("/Users/pgreze/git/pgreze/kyper/kyper/build/libs/kyper.jar")
// @file:DependsOn("com.github.pgreze:kyper:WIP")

import kyper.kyper

kyper(help = "Run multiple commands from Kotlin script with ease") {
    register("time", "Display current timestamp") { ->
        println(System.currentTimeMillis())
    }

    register("hello") { name ->
        println("Hello $name")
    }
}.invoke(args)
