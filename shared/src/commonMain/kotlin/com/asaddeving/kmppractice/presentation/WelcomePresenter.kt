package com.asaddeving.kmppractice.presentation

import com.asaddeving.kmppractice.domain.usecase.GetWelcomeMessageUseCase

/** Shared presentation logic: turns domain data into ready-to-render UI text for both apps. */
class WelcomePresenter(
    private val getWelcomeMessage: GetWelcomeMessageUseCase,
) {
    fun loadUiState(): WelcomeUiState {
        val message = getWelcomeMessage()
        return WelcomeUiState(
            title = message.greeting,
            subtitle = "Running on ${message.platformName} · timestamp ${message.createdAtMillis} ms",
        )
    }
}
