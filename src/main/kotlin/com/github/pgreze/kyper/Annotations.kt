package com.github.pgreze.kyper

/**
 * Add help message for:
 * - a function registered with [Kyper.register]
 * - a parameter of a registered function.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
public annotation class Help(val help: String)

// TODO: support exists
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
private annotation class Exists(val exists: Boolean = true)
