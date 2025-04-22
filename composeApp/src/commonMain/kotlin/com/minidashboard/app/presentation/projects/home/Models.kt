package com.minidashboard.app.presentation.projects.home

interface ProjectsScreenRouter {
    fun onTap(project: String)
    fun onNewProject()
}

/**
 *
 */
interface ProjectsCardsRouter {
    fun onTap(project: String)
    fun onNewProject()
}