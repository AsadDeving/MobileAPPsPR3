package com.asaddeving.kmppractice.platform

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

private const val MILLIS_PER_SECOND = 1_000

// timeIntervalSince1970 is in SECONDS (Double), so scale it to match Android's milliseconds.
actual fun currentTimestamp(): Long = (NSDate().timeIntervalSince1970 * MILLIS_PER_SECOND).toLong()
