package com.minidashboard.app.presentation.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomEndFAB(
    onTap: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()  // Makes the Box fill the whole screen
    ) {
        // FloatingActionButton aligned to the bottom-end of the screen
        FloatingActionButton(
            onClick = { onTap() },
            modifier = Modifier
                .align(Alignment.BottomEnd)  // Align to the bottom-end
                .padding(16.dp)  // Padding from the edges
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }
}