package com.minidashboard.app.presentation.projects.create

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import minidashboard.composeapp.generated.resources.Res
import minidashboard.composeapp.generated.resources.projects_create_description
import minidashboard.composeapp.generated.resources.projects_create_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProjectCreateScreen(
    onBackPressed: () -> Unit,
){
    val viewModel = koinViewModel<ProjectCreateViewModel>()
    val _state by viewModel.state.collectAsState()

    when(val state = _state){
        is ProjectCreateState.State -> {
            FormScreen(
                formState = state,
                viewModel = viewModel,
                onBackPressed = onBackPressed
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.proccesAction(ProjectCreateActions.Reset)
        }
    }
}

@Composable
fun FormScreen(
    formState: ProjectCreateState.State,
    viewModel: ProjectCreateViewModel,
    onBackPressed: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = formState.project,
            onValueChange = { viewModel.updateTextFieldProject(it) },
            label = { Text( stringResource(Res.string.projects_create_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = formState.description,
            onValueChange = { viewModel.updateTextFieldDescription(it) },
            label = { Text(stringResource(Res.string.projects_create_description)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                viewModel.proccesAction(ProjectCreateActions.Save)
                onBackPressed()
            },
            //enabled = formState.isFormValid,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Submit")
        }
    }
}