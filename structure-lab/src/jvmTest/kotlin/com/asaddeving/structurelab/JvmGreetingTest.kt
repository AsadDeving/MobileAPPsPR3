package com.asaddeving.structurelab

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JvmGreetingTest {
    @Test
    fun jvmMainSeesCommonMain() {
        assertEquals("Hello from commonMain (JVM)", jvmGreeting())
    }

    @Test
    fun jvmMainCanUseJavaIo() {
        assertTrue(tempDirectoryExists())
    }
}
