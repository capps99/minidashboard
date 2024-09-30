package com.minidashboard.app.presentation.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HelpIcon(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    // State to control the visibility of the dialog
    var showDialog by remember { mutableStateOf(false) }

    // Icon that triggers the help dialog
    IconButton(onClick = { showDialog = true }) {
        Icon(
            imageVector = Icons.Filled.Info, // Info icon from Material Icons
            contentDescription = "Help Icon",
            modifier = modifier.size(24.dp)
        )
    }

    // Show the help dialog when the icon is clicked
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // Close the dialog when clicked outside or back pressed
            title = {
                Text(text = title)
            },
            text = {
                Text(text = description)
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewHelpIcon() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Press the info icon for help")
        Spacer(modifier = Modifier.height(8.dp))
        HelpIcon(
            title = "title",
            description = "description"
        ) // Display the help icon
    }
}