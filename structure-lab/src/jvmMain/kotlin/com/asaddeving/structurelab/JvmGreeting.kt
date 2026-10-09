package com.asaddeving.structurelab

import java.io.File

fun jvmGreeting(): String = "${jvmAndJsGreeting()} (JVM)"

/** java.io.File compiles here because jvmMain targets only the JVM (Q7). */
fun tempDirectoryExists(): Boolean = File(System.getProperty("java.io.tmpdir")).isDirectory
