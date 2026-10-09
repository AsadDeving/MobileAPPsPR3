package com.asaddeving.kmppractice.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class GreetingTest {
    @Test
    fun greetReturnsExpectedText() {
        assertEquals("Hello, Kotlin Multiplatform!", Greeting().greet())
    }
}
