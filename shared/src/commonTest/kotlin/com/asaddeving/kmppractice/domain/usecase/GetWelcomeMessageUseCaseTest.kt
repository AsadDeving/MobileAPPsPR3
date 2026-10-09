package com.asaddeving.kmppractice.domain.usecase

import com.asaddeving.kmppractice.FakePlatformRepository
import com.asaddeving.kmppractice.domain.model.Greeting
import com.asaddeving.kmppractice.domain.model.WelcomeMessage
import kotlin.test.Test
import kotlin.test.assertEquals

class GetWelcomeMessageUseCaseTest {
    @Test
    fun combinesGreetingWithPlatformFacts() {
        val useCase = GetWelcomeMessageUseCase(FakePlatformRepository(name = "TestOS", timeMillis = 42))

        val expected = WelcomeMessage(
            greeting = Greeting.TEXT,
            platformName = "TestOS",
            createdAtMillis = 42,
        )
        assertEquals(expected, useCase())
    }
}
