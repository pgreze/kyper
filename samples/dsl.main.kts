#!/usr/bin/env kotlinc -script

@file:DependsOn("/Users/pgreze/git/pgreze/kyper/build/libs/kyper.jar")
@file:Suppress("OPT_IN_USAGE")

import com.github.pgreze.kyper.kyper

kyper(help = "Run multiple commands from Kotlin script with ease") {
    register(name = "time", help = "Display current timestamp") { ->
        println(System.currentTimeMillis())
    }

    register(name = "hello") { name ->
        println("Hello $name")
    }
}.invoke(args)
