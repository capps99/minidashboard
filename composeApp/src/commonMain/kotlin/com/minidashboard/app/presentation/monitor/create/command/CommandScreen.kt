package com.minidashboard.app.presentation.monitor.create.command

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minidashboard.app.data.models.*
import com.minidashboard.app.presentation.monitor.widget.ScheduleWidget
import com.minidashboard.app.presentation.widgets.DialogListWidget
import com.minidashboard.app.presentation.widgets.ExpandableColumn
import com.minidashboard.app.presentation.widgets.HelpIcon
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CommandScreen(
    cronProcess: CronProcess?,
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
        cronProcess = cronProcess,
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
    cronProcess: CronProcess?,
    onCreated: (CronProcess) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<CommandScreenViewModel>()

    // State variables for the form fields
    var title by remember { mutableStateOf(cronProcess?.common?.title ?: "") }
    var description by remember { mutableStateOf(cronProcess?.common?.description ?:"") }
    var command by remember { mutableStateOf("") }
    var submitEnabled by remember { mutableStateOf(false) }
    // Validate form fields to enable submit button
    submitEnabled = title.isNotEmpty() && command.isNotEmpty()

    var _cronSchedule: CronSchedule? = null

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

            ScheduleWidget { cronSchedule ->
                Napier.d {"CronSchedule created: $cronSchedule"}
                _cronSchedule = cronSchedule
            }


            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        val schedule = _cronSchedule ?: run {
                            Napier.d { "Schedule cannot be empty." }
                            return@Button
                        }

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
                                    schedule = schedule,
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
                        val schedule = _cronSchedule ?: run {
                            Napier.d { "Schedule cannot be empty." }
                            return@Button
                        }

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
                                schedule = schedule,
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