package com.asaddeving.kmppractice.domain.model

data class WelcomeMessage(
    val greeting: String,
    val platformName: String,
    val createdAtMillis: Long,
)
