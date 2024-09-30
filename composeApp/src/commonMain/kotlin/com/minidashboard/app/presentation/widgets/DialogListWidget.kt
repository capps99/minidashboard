package com.minidashboard.app.presentation.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun DialogListWidget(
    options: List<Pair<String, String?>>,
    isShowing: Boolean,
    onClosed: (Pair<String, String?>) -> Unit
) {
    var selectedOption by remember { mutableStateOf(options.first()) }

    Column {
        if (isShowing) {
            AlertDialog(
                onDismissRequest = { onClosed(selectedOption) },
                title = { Text(text = "Choose an Option") },
                text = {
                    Column {
                        options.forEach { option ->
                            TextButton(onClick = {
                                selectedOption = option
                                onClosed(selectedOption)
                            }) {
                                Text(
                                    text = option.first,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}