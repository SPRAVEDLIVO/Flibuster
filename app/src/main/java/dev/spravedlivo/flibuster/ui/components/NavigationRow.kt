package dev.spravedlivo.flibuster.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NavigationRow(onSettings: () -> Unit) {
    Button(onClick = { onSettings() }) {
        Text("Settings")
    }
}