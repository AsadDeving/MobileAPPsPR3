package com.asaddeving.kmppractice.platform

/** Describes the platform the shared code is currently running on. */
interface Platform {
    val name: String
}

/** Each platform source set provides its own `actual` implementation. */
expect fun getPlatform(): Platform
