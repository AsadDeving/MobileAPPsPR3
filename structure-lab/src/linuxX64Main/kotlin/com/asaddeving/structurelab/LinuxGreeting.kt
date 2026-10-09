package com.asaddeving.structurelab

// Calling jvmAndJsGreeting() here would be an "Unresolved reference": linuxX64 is not wired to jvmAndJsMain.
fun linuxGreeting(): String = "${commonGreeting()} (Linux)"
