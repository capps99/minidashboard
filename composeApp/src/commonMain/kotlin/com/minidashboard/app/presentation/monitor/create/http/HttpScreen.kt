package com.minidashboard.app.presentation.monitor.create.http

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.minidashboard.app.data.models.*
import com.minidashboard.app.presentation.widgets.ExpandableColumn
import com.minidashboard.app.presentation.widgets.HelpIcon
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HttpScreen(
    modifier: Modifier = Modifier,
    onCreated: (CronProcess) -> Unit = {},
) {
    val viewModel = koinViewModel<HttpScreenViewModel>()
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    when (state) {
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
                is HttpResult -> {
                    HttpResultContainer(result)
                }

                else -> {}
            }
        }

        is HttpScreenState.Messages -> {
            AlertDialog(
                onDismissRequest = {
                    viewModel.processAction(TestMonitorAction.hideError)
                }, // Close the dialog when clicked outside or back pressed
                title = {
                    Text(text = s.title)
                },
                text = {
                    Text(text = s.message)
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.processAction(TestMonitorAction.hideError)
                    }) {
                        Text("OK")
                    }
                }
            )
        }

        HttpScreenState.Initial -> {}
        HttpScreenState.Loading -> {}
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

    // show/hide dialog schedule
    var showScheduleTypeDialog by remember { mutableStateOf(false) }
    var showScheduleTypeSelected: Pair<String, String> by remember { mutableStateOf("Select a Time Interval" to "") }
    var scheduleTime by remember { mutableStateOf("") }

    var ruleCode by remember { mutableStateOf("200") }

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

            ExpandableColumn(
                title = "Rules"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("HTTP response = ")
                    TextField(
                        value = ruleCode,
                        onValueChange = { ruleCode = it },
                    )
                    HelpIcon(
                        title = "Response Code",
                        description = "Put the code here that you wwant to match.",
                    )
                }
            }

            ExpandableColumn(
                title = "Schedule"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = scheduleTime,
                        onValueChange = { scheduleTime = it },
                        label = { Text("Days/Hours/Minutes") },
                        singleLine = true,
                        modifier = Modifier.weight(0.30f)
                    )
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.30f)
                    ) {
                        Text("Type: ")
                        Button(
                            onClick = { showScheduleTypeDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(showScheduleTypeSelected.first)
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        viewModel.processAction(
                            TestMonitorAction.Test(
                                HttpCronProcess(
                                    common = CronCommmon(
                                        title = title,
                                        description = description,
                                        active = true,
                                    ),
                                    setup = HttpSetupConfig(
                                        url = url
                                    ),
                                    schedule = CronSchedule(
                                        time = scheduleTime.toIntOrNull() ?: 0,
                                        interval = showScheduleTypeSelected.second
                                    ),
                                    rules = viewModel.createRules(
                                        httpMatchCode = ruleCode
                                    ),
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
                                common = CronCommmon(
                                    title = title,
                                    description = description,
                                    active = true,
                                ),
                                setup = HttpSetupConfig(
                                    url = url
                                ),
                                schedule = CronSchedule(
                                    time = scheduleTime.toIntOrNull() ?: 0,
                                    interval = showScheduleTypeSelected.second
                                ),
                                rules = viewModel.createRules(
                                    httpMatchCode = ruleCode
                                ),
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