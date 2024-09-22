package com.minidashboard.app.presentation.widgets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable

@Composable
fun TemplateScreen(
    title: String,
    onBackPressed: () -> Unit = {},
    content: @Composable @UiComposable (PaddingValues) -> Unit,
){
    MaterialTheme {
        Scaffold(
            topBar = {
                ToolbarView(
                    title = title,
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier.fillMaxHeight(),
                            onClick = { onBackPressed() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = ""//Res.string.back.toString()
                            )
                        }
                                     },
                    actions = {

                    }
                )
                     },
            content = { padding ->
                content(padding)
            })
    }
}