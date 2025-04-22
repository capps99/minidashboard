package com.minidashboard.app.presentation.projects.create

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProjectCreateScreen(){
    val viewModel = koinViewModel<ProjectCreateViewModel>()

}