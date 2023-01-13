package com.github.pgreze.kyper

/** Register a function as a kyper command. */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
public annotation class Command(val help: String = "")

/** Add metadata for a function parameter annotated with [Command]. */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
public annotation class Parameter(val help: String)

// TODO: support exists
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
private annotation class Exists(val exists: Boolean = true)
