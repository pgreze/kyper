#!/usr/bin/env kotlinc -script

// TODO: generates a copy of samples from a Gradle task for integration tests

val samplesFolder: java.io.File = __FILE__.parentFile
val jarFile = samplesFolder.parentFile.resolve("build/libs/")
    .listFiles()
    ?.firstOrNull { it.name.matches("kyper-[^-]+.jar".toRegex()) }
    ?: throw NullPointerException("Local jar not found")
val localDependsOn = "@file:DependsOn(\"/${jarFile.absolutePath}\")"
val remoteDependsOn = "@file:DependsOn(\"com.github.pgreze:kyper:0.3\")"

samplesFolder.listFiles()?.filter { it.name.endsWith(".kts") }?.forEach { file ->
    var dependencyFound = false
    val lines = file.readLines().map { line ->
        if (line.trim().startsWith("@file:DependsOn(")) {
            when {
                line.contains("build/libs/kyper.*.jar\"\\)".toRegex()) -> {
                    dependencyFound = true
                    return@map remoteDependsOn
                }

                line.contains("com.github.pgreze:kyper") -> {
                    dependencyFound = true
                    return@map localDependsOn
                }
            }
        }
        line
    }
    if (dependencyFound) {
        println("Overwrite $file")
        file.writeText(lines.joinToString("\n") + "\n")
    } else {
        println("Ignore $file")
    }
}
