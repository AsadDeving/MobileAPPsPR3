package com.asaddeving.kmppractice.domain.repository

/** Domain-side port: the domain asks for facts, the data layer decides where they come from. */
interface PlatformRepository {
    fun platformName(): String
    fun currentTimeMillis(): Long
}
