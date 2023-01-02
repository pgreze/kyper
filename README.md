# kyper [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![](https://github.com/pgreze/kyper/actions/workflows/main.yml/badge.svg)](https://github.com/pgreze/kyper/actions/workflows/main.yml) [![codecov](https://codecov.io/gh/pgreze/kotlin-process/branch/main/graph/badge.svg?token=PDyl2T0EEB)](https://codecov.io/gh/pgreze/kyper)

Functional Kotlin friendly way to create command line applications.

It's coming from the basic need of having a function like:

```kotlin
fun main(
    content: String,
    target: File,
    mode: Mode = Mode.APPEND,
) {
    TODO()
}

enum class Mode { APPEND, OVERWRITE }
```

with all the expected behaviors we're expecting when using it in our code:
- support more than String like File/Path/Enum/etc,
- mode being an optional parameter,

and turns it into a command line application powered by Kotlin script.

This library is solving this need 🪄

> Kyper? Is it a name?

This library is hugely inspired by the wonderful [typer](https://typer.tiangolo.com/)
from the Python ecosystem.

Also naming is hard 😇

> But we already have [clikt](https://ajalt.github.io/clikt/)? (or any alternative)

Correct, but I try to keep my Kotlin scripts as small as possible,
and have to deal with classes is not what I would describe as simple.

Also chained property delegates are great,
but I always need to read the documentation to figure out all the options.

> So let's migrate everything to this wonderful library?

For simple use cases like Kotlin scripts, feel free.

For more complex applications, where readability is important,
I would stick with [clikt](https://ajalt.github.io/clikt/)
or any alternative not working on ~magic~ reflection
and/or implicit behaviors as this library is doing.

## Installation  [![central](https://maven-badges.herokuapp.com/maven-central/com.github.pgreze/kyper/badge.svg?style={style})](https://search.maven.org/artifact/com.github.pgreze/kyper) ![](https://img.shields.io/badge/Java-11-blue) [![](https://img.shields.io/badge/Kotlin-1.7.22-blue)](https://kotlinlang.org/)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    // Check the 🔝 maven central badge 🔝 for the latest $kyperVersion
    implementation("com.github.pgreze:kyper:$kyperVersion")
}
```

Or in your kotlin script:

```kotlin
@file:DependsOn("com.github.pgreze:kyper:$kyperVersion")
```

## Usage with function(s)

### Start with a single function

We can start by defining a simple function handling our logic:

```kotlin
#!/usr/bin/env kotlinc -script

import com.github.pgreze.kyper.Help
import com.github.pgreze.kyper.kyper

@Help("function help message")
fun main(
    @Help("the name to greet")
    name: String
) {
    println("hello $name")
}

kyper().invoke(args)
```

And run it with the name parameter:

```bash
$ ./script.main.kts there
hello there
```

Notice we also defined help messages for both the command and its parameter:

```bash
$ # Use `--` to indicates following arguments to the script, not kotlinc itself
$ ./script.main.kts -- --help
Usage: main NAME

  function help message

Options:
  -h, --help      Show this message and exit

Arguments:
  NAME  the name to greet
```

### Only public methods are exported as commands

Our script can declare more internal/private methods,
without exposing them as command:

```kotlin
#!/usr/bin/env kotlinc -script

import com.github.pgreze.kyper.kyper

fun main(name: String) {
    greet(name)
}

private fun greet(name: String) {
    println("Hello $name")
}

kyper().invoke(args)
```

Usage is the same:

```bash
$ ./script.main.kts there
hello there
```

But having several `public` functions will turn our application into a multi-command mode:

```kotlin
#!/usr/bin/env kotlinc -script

import com.github.pgreze.kyper.kyper

@Help("Say hello in English")
fun hello(name: String) {
    println("hello $name")
}

@Help("Say hello in French")
fun bonjour(name: String) {
    println("bonjour $name")
}

kyper(help = "Say hello to your user").invoke(args)
```

We can now notice several commands are available by calling --help:

```bash
$ ./kyper/hellos.main.kts -- --help
Usage: [OPTIONS] COMMAND [ARGS]...

  Say hello to your user

Options:
  -h, --help      Show this message and exit

Commands:
  BONJOUR  Say hello in French
  HELLO    Say hello in English
```

Each command can be requested for `--help`:
```
$ ./kyper/hellos.main.kts -- --help bonjour
Usage: bonjour NAME

  Say hello in French

Options:
  -h, --help      Show this message and exit

Arguments:
  NAME
```

### Handle more than strings as arguments

Following types are supported:

```kotlin
fun main(
    string: String = "arg",
    int: Int = 1,
    float: Float = 1.2f,
    double: Double = 3.14,
    long: Long = Long.MAX_VALUE,
    boolean: Boolean = true,
    bigInteger: BigInteger = BigInteger.valueOf(12),
    bigDecimal: BigDecimal = BigDecimal.valueOf(12.3),
    file: File = File("file"),
    path: Path = Path.of("path"),
    choice: Choice = Choice.NO,
) {
    TODO()
}

enum class Choice { YES, NO }

kyper().invoke(args)
```

Noticed we provided default values for each value,
so each of these parameters are optionals as well.

## Usage with lambda(s) (experimental)

Maybe the Kotlin DSL syntax is something you're looking for in your Kotlin script,
and so this library is also providing a similar syntax based on lambdas:

```kotlin
#!/usr/bin/env kotlinc -script

@file:DependsOn("/Users/pgreze/git/pgreze/kyper/kyper/build/libs/kyper.jar")
@file:Suppress("OPT_IN_USAGE")

import com.github.pgreze.kyper.kyper

kyper(help = "Run multiple commands from Kotlin script with ease") {
    register(name = "time", help = "Display current timestamp") { ->
        println(System.currentTimeMillis())
    }

    register(name = "greet") { name ->
        println("Hello $name")
    }
}.invoke(args)
```

But this is coming with restrictions:
- no default argument(s),
- up to 4 arguments,
- only String is supported.

This may be dropped in the future if we cannot reach
the same support as with the functions based usage.
