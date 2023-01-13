#!/usr/bin/env kotlinc -script

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("com.github.pgreze:kyper:0.3")

import com.github.pgreze.kyper.Command
import com.github.pgreze.kyper.Parameter
import com.github.pgreze.kyper.kyper

@Command("Say hello in English")
fun hello(name: String) {
    println("hello $name")
}

@Command("Say hello in French")
fun bonjour(name: String) {
    println("bonjour $name")
}

kyper(help = "Say hello to your user").invoke(args)
