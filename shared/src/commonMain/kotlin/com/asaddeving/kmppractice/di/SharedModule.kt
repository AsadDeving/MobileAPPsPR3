package com.asaddeving.kmppractice.di

import com.asaddeving.kmppractice.data.repository.DevicePlatformRepository
import com.asaddeving.kmppractice.domain.repository.PlatformRepository
import com.asaddeving.kmppractice.domain.usecase.GetWelcomeMessageUseCase
import com.asaddeving.kmppractice.presentation.WelcomePresenter

/** Composition root: the only place that knows how the layers are wired together. */
object SharedModule {
    private val platformRepository: PlatformRepository by lazy { DevicePlatformRepository() }

    private val getWelcomeMessageUseCase: GetWelcomeMessageUseCase by lazy {
        GetWelcomeMessageUseCase(platformRepository)
    }

    fun provideWelcomePresenter(): WelcomePresenter = WelcomePresenter(getWelcomeMessageUseCase)
}
