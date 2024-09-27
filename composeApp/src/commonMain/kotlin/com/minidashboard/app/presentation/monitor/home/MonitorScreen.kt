package com.minidashboard.app.presentation.monitor.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.presentation.widgets.FloatButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MonitorScreen(
    onCreateMonitor: () -> Unit = {},
) {
    val viewModel = koinViewModel<MonitorViewModel>()
    val state by viewModel.state.collectAsState()

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
                }
            )
        }

        MonitorState.Initial -> {}
    }

    LaunchedEffect(Unit) {
        viewModel.processAction(MonitorActions.Load)
    }
}

@Composable
fun DataContent(
    data: MonitorState.Data,
    onCreateMonitor: () -> Unit = {},
    onStatusProccess: (Boolean) -> Unit = {},
) {
    val listState = rememberLazyListState()
    var isPlaying by remember { mutableStateOf(false) }

    // A Box to overlay the LazyColumn with a vertical scroll indicator
    Box(modifier = Modifier.fillMaxSize()) {
        // The LazyColumn displaying the list of items
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(data.crons) { item ->
                ListItemView(item)
                
            }
        }
        FloatButton(
            icon = Icons.Default.Add,
            onTap = { onCreateMonitor() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
        )
        FloatButton(
            icon = when (isPlaying) {
                true -> Icons.Default.Build
                false -> Icons.Default.PlayArrow
            },
            onTap = {
                isPlaying = !isPlaying
                onStatusProccess(isPlaying)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ListItemView(item: CronProcess) {
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
            // Title
            Text(
                text = item.common.title,
                style = MaterialTheme.typography.body1,
                color = Color.Black
            )

            // Description
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.common.description,
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Row of color indicators based on the list of statuses
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Iterate over each status and create a colored box
                /*item.statuses.forEach { status ->
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
                }*/
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
