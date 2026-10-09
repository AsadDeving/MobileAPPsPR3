package com.asaddeving.kmppractice.platform

/** Milliseconds since 1970-01-01 UTC, read from each platform's own clock. */
expect fun currentTimestamp(): Long
