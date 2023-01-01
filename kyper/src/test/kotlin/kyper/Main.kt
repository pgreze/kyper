package kyper

import java.io.File

object Main {
    fun main() {
        println("Now: ${System.currentTimeMillis()}")
    }

    internal fun mainI() {
        println("Now: ${System.currentTimeMillis()}")
    }

    private fun mainP() {
        println("Now: ${System.currentTimeMillis()}")
    }
}

fun main(args: Array<String>) {
    val kyper = kyper {
        registerPublicMethods(Main)
        register(::time)
        register(::greet)
        register(::command2)
        register("lambda0") { -> println("hello world") }
        register("lambda1") { name: String ->
            println("hello $name")
        }
        register("lambda2") { name: String, end: String -> println("hey $name $end") }
    }

//    kyper("greet", "monde", "OK")
//    kyper("time")
//    kyper("main")
//    kyper("command2", "haha", "42", ".")
//    kyper("lambda0")
//    kyper("lambda1", "world")
//    kyper("lambda2", "haha", "42")
    kyper("--help")
    kyper("greet", "--help")
//    kyper(args)
}

fun time() {
    println("Now: ${System.currentTimeMillis()}")
}

@Help("This is greeting someone")
fun greet(
    @Help("The name to greet")
    name: String,
    @Help("OK or NO?")
    choice: Choice = Choice.OK,
    flag: Boolean = false,
) {
    println("Hello $name $choice $flag")
}

@Help("Wonderful help message")
fun command2(
    arg1: String,
    arg2: Int,
    @Exists
    file: File,
) {
    println("arg1=$arg1 arg2=$arg2 file=${file.canonicalPath}")
}
