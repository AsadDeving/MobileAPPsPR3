package com.asaddeving.kmppractice

import com.asaddeving.kmppractice.domain.repository.PlatformRepository

class FakePlatformRepository(
    private val name: String = "TestOS",
    private val timeMillis: Long = 1_700_000_000_000,
) : PlatformRepository {
    override fun platformName(): String = name

    override fun currentTimeMillis(): Long = timeMillis
}
