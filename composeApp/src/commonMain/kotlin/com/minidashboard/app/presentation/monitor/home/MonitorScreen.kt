package com.minidashboard.app.presentation.monitor.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.presentation.widgets.FloatButton
import io.github.aakira.napier.Napier
import minidashboard.composeapp.generated.resources.Res
import minidashboard.composeapp.generated.resources.pause
import minidashboard.composeapp.generated.resources.play
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MonitorScreen(
    onCreateMonitor: () -> Unit = {},
    onEditProccess: (CronItem) -> Unit = {},
) {
    val viewModel = koinViewModel<MonitorViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.processAction(MonitorActions.Load)
    }
    
    when (val data = state) {
        is MonitorState.Data -> {
            DataContent(
                data = data,
                onCreateMonitor = { onCreateMonitor() },
                onStatusProccess = { status ->
                    if (status) {
                        viewModel.processAction(MonitorActions.Start)
                    } else {
                        viewModel.processAction(MonitorActions.Stop)
                    }
                },
                onEditProccess = { cronItem ->
                    onEditProccess(cronItem)
                }
            )
        }

        MonitorState.Initial -> {}
    }
}

@Composable
fun DataContent(
    data: MonitorState.Data,
    onCreateMonitor: () -> Unit = {},
    onStatusProccess: (Boolean) -> Unit = {},
    onEditProccess: (CronItem) -> Unit = {},
) {
    val viewModel = koinViewModel<MonitorViewModel>()
    val listState = rememberLazyListState()

    val list = data.crons.values.toList()

    // A Box to overlay the LazyColumn with a vertical scroll indicator
    Box(modifier = Modifier.fillMaxSize()) {
        // The LazyColumn displaying the list of items
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(list) { item ->
                ListItemView(item) {
                    onEditProccess(it)
                }
            }
        }
        FloatButton(
            icon = Icons.Default.Add,
            description = "add",
            onTap = { onCreateMonitor() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
        )
        FloatButton(
            icon = when (data.isProcessRuning) {
                true -> painterResource(resource = Res.drawable.pause)
                false -> painterResource(resource = Res.drawable.play)
            },
            description = "play/stop",
            onTap = {
                val running = !data.isProcessRuning
                onStatusProccess(running)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ListItemView(
    item: CronItem,
    onEdit: (CronItem) -> Unit,
) {
    val cron = item.cron

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f) // This pushes the Button to the end
                ) {
                    // Title
                    Text(
                        text = cron.common.title,
                        style = MaterialTheme.typography.body1,
                        color = Color.Black
                    )

                    // Description
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = cron.common.description,
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                }

                IconButton(
                    onClick = {
                        onEdit(item)
                    },
                ){
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp) // Set size of the icon
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row of color indicators based on the list of statuses
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Iterate over each status and create a colored box
                item.statuses.forEach { status ->
                    Box(
                        modifier = Modifier
                            .weight(1f)  // Equal weight for each box
                            .fillMaxHeight()
                            .background(
                                color = when (status) {
                                    Status.CORRECT -> Color.Green
                                    Status.WARNING -> Color.Yellow
                                    Status.ERROR -> Color.Red
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Add spacing between boxes
                }
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        MonitorScreen()
    }
}
