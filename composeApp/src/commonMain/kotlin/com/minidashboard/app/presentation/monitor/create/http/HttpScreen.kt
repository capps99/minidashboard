package com.minidashboard.app.presentation.monitor.create.http

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.minidashboard.app.data.models.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HttpScreen(
    modifier: Modifier = Modifier,
    onCreated: (CronProcess) -> Unit = {},
) {
    val viewModel = koinViewModel<HttpScreenViewModel>()
    val state by viewModel.state.collectAsState()

    when(state) {
        HttpScreenState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        else -> {}
    }



    HttpScreenContent(
        modifier = modifier,
        onCreated = onCreated,
    )

    when (val s = state) {
        is HttpScreenState.Data -> {
            when (val result = s.result) {
                is HttpResult -> HttpResultContainer(result)
            }
        }
        else -> {}
    }
}

@Composable
fun HttpScreenContent(
    onCreated: (CronProcess) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<HttpScreenViewModel>()

    // State variables for the form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var submitEnabled by remember { mutableStateOf(false) }
    // Validate form fields to enable submit button
    submitEnabled = title.isNotEmpty() && url.isNotEmpty()

    Box {
        Column {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        viewModel.processAction(
                            TestMonitorAction.Test(
                                HttpCronProcess(
                                    cronCommmon = CronCommmon(
                                        title = title,
                                        description = description,
                                        active = true
                                    ),
                                    setup = HttpSetupConfig(
                                        url = url
                                    )
                                )
                            )
                        )
                    },
                ) {
                    Text("Test")
                }

                // Submit Button
                Button(
                    onClick = {
                        onCreated(
                            HttpCronProcess(
                                cronCommmon = CronCommmon(
                                    title = title,
                                    description = description,
                                    active = true
                                ),
                                setup = HttpSetupConfig(
                                    url = url
                                )
                            )
                        )
                    },
                    //        enabled = submitEnabled,
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun HttpResultContainer(result: HttpResult) {
    Column {
        Row {
            Text("Response Code:")
            Text(result.code.toString())
        }
        Text("Response:")
        Text(result.message)
    }
}