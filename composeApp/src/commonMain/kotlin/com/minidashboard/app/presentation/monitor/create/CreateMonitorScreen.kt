package com.minidashboard.app.presentation.monitor.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minidashboard.app.data.models.CommandSetupConfig
import com.minidashboard.app.data.models.HttpSetupConfig
import com.minidashboard.app.data.models.PythonSetupConfig
import com.minidashboard.app.data.models.WebSocketSetupConfig
import com.minidashboard.app.presentation.monitor.create.command.CommandScreen
import com.minidashboard.app.presentation.monitor.create.http.HttpScreen
import com.minidashboard.app.presentation.widgets.DialogListWidget
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateMonitorScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<CreateMonitorViewModel>()
    var showTypeDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()  // Remember the scroll state
    var typeSelected: Pair<String, String?> by remember { mutableStateOf("Select a Cron Type" to "") }

    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)  // Add vertical scroll functionality
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (val s = state) {
            is CreateMonitorState.Data -> Text("value ${s.attempt}")
            CreateMonitorState.Initial -> {}
        }

        // Type Button
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Type: ")
            Button(
                onClick = { showTypeDialog = true },
                modifier = modifier.fillMaxWidth()
            ) {
                Text(typeSelected.first)
            }
        }


        when(typeSelected.first){
            "Http" -> {
                HttpScreen {
                    viewModel.processAction(
                        action = CreateMonitorAction.Create(it)
                    )
                }
            }
            "Command" -> {
                CommandScreen {
                    viewModel.processAction(
                        action = CreateMonitorAction.Create(it)
                    )
                }
            }
        }

        DialogListWidget(
            options = listOfNotNull(
                "Select an option" to "",
                "Http" to HttpSetupConfig::class.simpleName,
                "WebSocket" to WebSocketSetupConfig::class.simpleName,
                "Python" to PythonSetupConfig::class.simpleName,
                "Command" to CommandSetupConfig::class.simpleName,
            ),
            isShowing= showTypeDialog
        ) { (label, type) ->
            typeSelected = Pair(label, type)
            showTypeDialog = false
        }
    }
}