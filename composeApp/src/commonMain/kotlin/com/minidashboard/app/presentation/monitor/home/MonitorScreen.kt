package com.minidashboard.app.presentation.monitor.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.minidashboard.app.presentation.widgets.BottomEndFAB
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
){
    val listState = rememberLazyListState()

    Text("attempt: ${data.attempt}")

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
    }

    BottomEndFAB {
        onCreateMonitor()
    }
}

@Composable
fun ListItemView(item: CronItem) {
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
                text = item.title,
                style = MaterialTheme.typography.body1,
                color = Color.Black
            )

            // Description
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
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
