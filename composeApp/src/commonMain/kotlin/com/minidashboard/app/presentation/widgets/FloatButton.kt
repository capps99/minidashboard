package com.minidashboard.app.presentation.widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FloatButton(
    icon: ImageVector,
    description: String,
    onTap: () -> Unit = {},
    modifier: Modifier = Modifier,
) {

    // FloatingActionButton aligned to the bottom-end of the screen
    FloatingActionButton(
        onClick = { onTap() },
        modifier = modifier
            .padding(16.dp)  // Padding from the edges
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}

@Composable
fun FloatButton(
    icon: Painter,
    description: String,
    onTap: () -> Unit = {},
    modifier: Modifier = Modifier,
) {

    // FloatingActionButton aligned to the bottom-end of the screen
    FloatingActionButton(
        onClick = { onTap() },
        modifier = modifier
            .padding(16.dp)  // Padding from the edges
    ) {
        Icon(
            painter = icon,
            contentDescription = description
        )
    }
}