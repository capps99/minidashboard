package com.minidashboard.app.presentation.monitor.create.command

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minidashboard.app.data.models.*
import com.minidashboard.app.presentation.widgets.DialogListWidget
import com.minidashboard.app.presentation.widgets.ExpandableColumn
import com.minidashboard.app.presentation.widgets.HelpIcon
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CommandScreen(
    modifier: Modifier = Modifier,
    onCreated: (CronProcess) -> Unit = {},
) {
    val viewModel = koinViewModel<CommandScreenViewModel>()
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    when (state) {
        CommandScreenState.Loading -> {
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

    CommandScreenContent(
        modifier = modifier,
        onCreated = onCreated,
    )

    when (val s = state) {
        is CommandScreenState.Data -> {}
        is CommandScreenState.Messages -> {
            AlertDialog(
                onDismissRequest = {
                    viewModel.processAction(CommandMonitorAction.hideError)
                }, // Close the dialog when clicked outside or back pressed
                title = {
                    Text(text = s.title)
                },
                text = {
                    Text(text = s.message)
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.processAction(CommandMonitorAction.hideError)
                    }) {
                        Text("OK")
                    }
                }
            )
        }

        CommandScreenState.Initial, CommandScreenState.Loading -> {}
    }
}

@Composable
fun CommandScreenContent(
    onCreated: (CronProcess) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<CommandScreenViewModel>()

    // State variables for the form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("") }
    var submitEnabled by remember { mutableStateOf(false) }
    // Validate form fields to enable submit button
    submitEnabled = title.isNotEmpty() && command.isNotEmpty()

    // show/hide dialog schedule
    var showScheduleTypeDialog by remember { mutableStateOf(false) }
    var showScheduleTypeSelected: Pair<String, String> by remember { mutableStateOf("Select a Time Interval" to "") }
    var scheduleTime by remember { mutableStateOf("") }

    var ruleExitCode by remember { mutableStateOf("0") }

    Box {
        Column {
            Text("Command Setup")
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
                value = command,
                onValueChange = { command = it },
                label = { Text("Command") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExpandableColumn(
                title = "Rules"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = ruleExitCode,
                        onValueChange = { ruleExitCode = it },
                        label = { Text("Exit code = ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HelpIcon(
                        title = "Exit code",
                        description = """
                        0 = Bash success return.
                        -1 = Bash thrown an error.
                        """.trimIndent(),
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
                    HelpIcon(
                        title = "Exit code",
                        description = """
                        0 = Bash success return.
                        -1 = Bash thrown an error.
                        """.trimIndent(),
                        modifier = Modifier.weight(0.10f, false)
                    )
                }
            }

            DialogListWidget(
                options = listOfNotNull(
                    "Select an option" to "",
                    "Minutes" to CronSchedule.MINUTES,
                    "Hours" to CronSchedule.HOURS,
                    "Days" to CronSchedule.DAYS,
                ),
                isShowing= showScheduleTypeDialog
            ) { (label, type) ->
                // Validate not null
                if(type.isNullOrEmpty()) return@DialogListWidget

                showScheduleTypeSelected = Pair(label, type)
                showScheduleTypeDialog = false
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        viewModel.processAction(
                            CommandMonitorAction.Test(
                                CommandCronProcess(
                                    common = CronCommmon(
                                        title = title,
                                        description = description,
                                        active = true,
                                    ),
                                    setup = CommandSetupConfig(
                                        command = command
                                    ),
                                    schedule = CronSchedule(
                                        time = scheduleTime.toIntOrNull() ?: 0,
                                        interval = showScheduleTypeSelected.second
                                    ),
                                    rules = viewModel.createRules(
                                        ruleExitCode = ruleExitCode
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
                            CommandCronProcess(
                                common = CronCommmon(
                                    title = title,
                                    description = description,
                                    active = true,
                                ),
                                setup = CommandSetupConfig(
                                    command = command
                                ),
                                schedule = CronSchedule(
                                    time = scheduleTime.toIntOrNull() ?: 0,
                                    interval = showScheduleTypeSelected.second
                                ),
                                rules = viewModel.createRules(
                                    ruleExitCode = ruleExitCode
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

/*
@Composable
fun CommandResultContainer(result: HttpResult) {
    Column {
        Row {
            Text("Response Code:")
            Text(result.code.toString())
        }
        Text("Response:")
        Text(result.message)
    }
}*/