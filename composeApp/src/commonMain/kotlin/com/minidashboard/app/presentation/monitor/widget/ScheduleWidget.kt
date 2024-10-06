package com.minidashboard.app.presentation.monitor.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.minidashboard.app.data.models.CronSchedule
import com.minidashboard.app.presentation.widgets.DialogListWidget
import com.minidashboard.app.presentation.widgets.ExpandableColumn
import com.minidashboard.app.presentation.widgets.HelpIcon

@Composable
fun ScheduleWidget(
    modifier: Modifier = Modifier,
    schedule: (CronSchedule) -> Unit = {},
) {
    // show/hide dialog schedule
    var showScheduleTypeDialog by remember { mutableStateOf(false) }
    var showScheduleTypeSelected: Pair<String, String> by remember { mutableStateOf("Select a Time Interval" to "") }
    var scheduleTime by remember { mutableStateOf("") }

    ExpandableColumn(
        title = "Schedule"
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = scheduleTime,
                onValueChange = {
                    scheduleTime = it

                    val interval = showScheduleTypeSelected.second ?: return@OutlinedTextField
                    val time = it.toIntOrNull() ?: return@OutlinedTextField
                    schedule(
                        CronSchedule(
                            time = time,
                            interval = interval,
                        )
                    )
                },
                label = { Text("Days/Hours/Minutes") },
                singleLine = true,
                modifier = Modifier.weight(0.30f)
            )
            Row(
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
        isShowing = showScheduleTypeDialog
    ) { (label, type) ->
        // Validate not null
        if (type.isNullOrEmpty()) return@DialogListWidget

        showScheduleTypeSelected = Pair(label, type)
        showScheduleTypeDialog = false

        val interval = showScheduleTypeSelected.second ?: return@DialogListWidget
        val time = scheduleTime.toIntOrNull() ?: return@DialogListWidget

        schedule(
            CronSchedule(
                time = time,
                interval = interval,
            )
        )
    }
}