package com.asaddeving.kmppractice.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asaddeving.kmppractice.presentation.WelcomeUiState

/** Pure UI: renders the state produced by the shared presenter and holds no logic of its own. */
@Composable
fun WelcomeScreen(uiState: WelcomeUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = uiState.title, style = MaterialTheme.typography.headlineSmall)
        Text(text = uiState.subtitle, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(WelcomeUiState(title = "Hello, Kotlin Multiplatform!", subtitle = "Running on Android"))
    }
}
