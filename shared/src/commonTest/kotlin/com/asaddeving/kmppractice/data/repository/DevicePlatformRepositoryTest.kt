package com.asaddeving.kmppractice.data.repository

import com.asaddeving.kmppractice.platform.Platform
import kotlin.test.Test
import kotlin.test.assertEquals

class DevicePlatformRepositoryTest {
    private val fakePlatform = object : Platform {
        override val name: String = "FakePlatform"
    }

    @Test
    fun delegatesToInjectedPlatformAndClock() {
        val repository = DevicePlatformRepository(platform = fakePlatform, clock = { 123L })

        assertEquals("FakePlatform", repository.platformName())
        assertEquals(123L, repository.currentTimeMillis())
    }
}
