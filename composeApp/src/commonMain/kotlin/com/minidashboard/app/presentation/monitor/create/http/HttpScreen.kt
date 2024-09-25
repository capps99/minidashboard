package com.minidashboard.app.presentation.monitor.create.http

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.minidashboard.app.data.models.CronCommmon
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.data.models.HttpCronProcess
import com.minidashboard.app.data.models.HttpSetupConfig
import com.minidashboard.app.presentation.monitor.create.TestMonitorAction
import com.minidashboard.app.presentation.monitor.create.TestMonitorViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HttpScreen(
    modifier: Modifier = Modifier,
    onCreated: (CronProcess) -> Unit = {},
) {
    val viewModel = koinViewModel<TestMonitorViewModel>()

    // State variables for the form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var submitEnabled by remember { mutableStateOf(false) }
    // Validate form fields to enable submit button
    submitEnabled = title.isNotEmpty() && url.isNotEmpty()


    Column {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            singleLine = true,
            modifier = modifier.fillMaxWidth()
        )
    }

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
        modifier = Modifier.fillMaxWidth()
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Save")
    }
}