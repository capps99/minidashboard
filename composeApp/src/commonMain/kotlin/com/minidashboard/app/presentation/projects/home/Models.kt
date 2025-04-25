package com.minidashboard.app.presentation.projects.home

import com.minidashboard.app.data.models.ProjectModel

interface ProjectsScreenRouter {
    fun onTap(project: ProjectModel)
    fun onNewProject()
}

/**
 *
 */
interface ProjectsCardsRouter {
    fun onTap(project: ProjectModel)
    fun onNewProject()
}