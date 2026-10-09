package com.asaddeving.structurelab

import kotlin.test.Test
import kotlin.test.assertEquals

class CommonGreetingTest {
    @Test
    fun commonGreetingIsSharedByAllTargets() {
        assertEquals("Hello from commonMain", commonGreeting())
    }
}
