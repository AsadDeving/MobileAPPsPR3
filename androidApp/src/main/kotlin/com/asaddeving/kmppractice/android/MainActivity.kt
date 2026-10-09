package com.asaddeving.kmppractice.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.asaddeving.kmppractice.di.SharedModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uiState = SharedModule.provideWelcomePresenter().loadUiState()
        setContent {
            MaterialTheme {
                WelcomeScreen(uiState)
            }
        }
    }
}
