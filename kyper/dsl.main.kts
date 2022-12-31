// TODO: make generic
@file:DependsOn("/Users/pgreze/git/pgreze/kyper/kyper/build/libs/kyper.jar")
// @file:DependsOn("com.github.pgreze:kyper:WIP")

import kyper.kyper

kyper {
    register("time") { ->
        println(System.currentTimeMillis())
    }

    register("hello") { name ->
        println("Hello $name")
    }
}.invoke(args)
