package com.asaddeving.kmppractice.domain.model

class Greeting {
    fun greet(): String = TEXT

    companion object {
        const val TEXT = "Hello, Kotlin Multiplatform!"
    }
}
