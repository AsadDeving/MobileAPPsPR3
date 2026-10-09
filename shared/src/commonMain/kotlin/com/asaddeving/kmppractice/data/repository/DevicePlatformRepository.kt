package com.asaddeving.kmppractice.data.repository

import com.asaddeving.kmppractice.domain.repository.PlatformRepository
import com.asaddeving.kmppractice.platform.Platform
import com.asaddeving.kmppractice.platform.currentTimestamp
import com.asaddeving.kmppractice.platform.getPlatform

/** Adapts the expect/actual platform APIs to the domain's [PlatformRepository] port. */
class DevicePlatformRepository(
    private val platform: Platform = getPlatform(),
    private val clock: () -> Long = ::currentTimestamp,
) : PlatformRepository {
    override fun platformName(): String = platform.name

    override fun currentTimeMillis(): Long = clock()
}
