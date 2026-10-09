package com.asaddeving.kmppractice.presentation

import com.asaddeving.kmppractice.FakePlatformRepository
import com.asaddeving.kmppractice.domain.usecase.GetWelcomeMessageUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class WelcomePresenterTest {
    @Test
    fun mapsWelcomeMessageToUiState() {
        val presenter = WelcomePresenter(
            GetWelcomeMessageUseCase(FakePlatformRepository(name = "TestOS", timeMillis = 7)),
        )

        val expected = WelcomeUiState(
            title = "Hello, Kotlin Multiplatform!",
            subtitle = "Running on TestOS · timestamp 7 ms",
        )
        assertEquals(expected, presenter.loadUiState())
    }
}
