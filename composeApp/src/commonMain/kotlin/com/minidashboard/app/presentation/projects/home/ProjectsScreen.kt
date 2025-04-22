package com.minidashboard.app.presentation.projects.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ProjectScreen(
    router: ProjectsCardsRouter
){
    val viewModel = koinViewModel<ProjectsViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit){
        viewModel.processAction(ProjectsActions.Load)
    }

    when(val state = state){
        is ProjectsState.Data -> listProjects(
            state = state,
            router = object : ProjectsScreenRouter {
                override fun onTap(project: String) {
                    Napier.d { "ProjectScreen onTap $project" }
                    when (project.startsWith("+")){
                        true -> onNewProject()
                        false -> router.onTap(project)
                    }

                }

                override fun onNewProject() {
                    router.onNewProject()
                }
            }
        )
        ProjectsState.Initial -> {
            // Nothing to do
        }
    }

}

@Composable
fun listProjects(
    state: ProjectsState.Data,
    router: ProjectsScreenRouter
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 columns
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),

    ) {
        items(state.projects) { item ->
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable {
                        router.onTap(item)
                    }
            ) {
                Column(
                    horizontalAlignment =  Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    CircularIconWithLetter(item)
                    Text(
                        text = item,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(25.dp)
                            .wrapContentHeight()
                    )
                }
            }
        }
    }
}

@Composable
fun CircularIconWithLetter(name: String, backgroundColor: Color = Color.Gray, size: Int = 55) {
    // Extract the first letter
    val firstLetter = if (name.isNotEmpty()) name[0].toString().uppercase() else "?"

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)  // Set the size of the circle
            .clip(CircleShape)  // Clip the box to a circular shape
            .background(backgroundColor)  // Set background color
    ) {
        // Display the first letter inside the circle
        Text(
            text = firstLetter,
            fontSize = (size / 2).sp,  // Set font size relative to circle size
            fontWeight = FontWeight.Bold,
            color = Color.White,  // Set text color
            textAlign = TextAlign.Center
        )
    }
}