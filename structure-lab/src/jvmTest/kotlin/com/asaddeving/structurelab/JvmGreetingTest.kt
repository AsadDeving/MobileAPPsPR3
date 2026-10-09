package com.asaddeving.structurelab

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JvmGreetingTest {
    @Test
    fun jvmSeesTheJvmAndJsSourceSet() {
        assertEquals("Shared by JVM and JS only (JVM)", jvmGreeting())
    }

    @Test
    fun jvmMainCanUseJavaIo() {
        assertTrue(tempDirectoryExists())
    }
}
