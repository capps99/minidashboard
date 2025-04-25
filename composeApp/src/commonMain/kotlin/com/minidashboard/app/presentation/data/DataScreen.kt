package com.minidashboard.app.presentation.data

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import minidashboard.composeapp.generated.resources.Res
import minidashboard.composeapp.generated.resources.home_data_clear
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
@Preview
fun DataScreen(){
    MaterialTheme {
        val viewModel = koinViewModel<DataViewModel>()
        Column(
            Modifier.fillMaxWidth(),
               horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                viewModel.processAction(DataActions.TapDelete)
            }) {
                Text(stringResource(Res.string.home_data_clear))
            }

        }
    }
}