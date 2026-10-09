package com.asaddeving.kmppractice.domain.usecase

import com.asaddeving.kmppractice.domain.model.Greeting
import com.asaddeving.kmppractice.domain.model.WelcomeMessage
import com.asaddeving.kmppractice.domain.repository.PlatformRepository

class GetWelcomeMessageUseCase(
    private val platformRepository: PlatformRepository,
    private val greeting: Greeting = Greeting(),
) {
    operator fun invoke(): WelcomeMessage = WelcomeMessage(
        greeting = greeting.greet(),
        platformName = platformRepository.platformName(),
        createdAtMillis = platformRepository.currentTimeMillis(),
    )
}
