package com.asaddeving.kmppractice.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AndroidPlatformTest {
    @Test
    fun actualPlatformIsAndroid() {
        assertEquals("Android", getPlatform().name)
    }

    @Test
    fun timestampIsInMilliseconds() {
        val before = System.currentTimeMillis()
        val timestamp = currentTimestamp()
        assertTrue(timestamp >= before)
    }
}
