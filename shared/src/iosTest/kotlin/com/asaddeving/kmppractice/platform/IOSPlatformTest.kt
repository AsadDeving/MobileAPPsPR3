package com.asaddeving.kmppractice.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val YEAR_2020_IN_MILLIS = 1_577_836_800_000L

class IOSPlatformTest {
    @Test
    fun actualPlatformIsIOS() {
        assertEquals("iOS", getPlatform().name)
    }

    @Test
    fun timestampIsInMillisecondsNotSeconds() {
        assertTrue(currentTimestamp() > YEAR_2020_IN_MILLIS)
    }
}
