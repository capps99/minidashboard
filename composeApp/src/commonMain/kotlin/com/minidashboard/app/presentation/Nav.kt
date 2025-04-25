package com.minidashboard.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.minidashboard.app.data.models.ProjectModel
import com.minidashboard.app.presentation.data.DataScreen
import com.minidashboard.app.presentation.home.HomeScreen
import com.minidashboard.app.presentation.monitor.create.CreateMonitorScreen
import com.minidashboard.app.presentation.monitor.home.MonitorScreen
import com.minidashboard.app.presentation.projects.create.ProjectCreateScreen
import com.minidashboard.app.presentation.projects.home.ProjectScreen
import com.minidashboard.app.presentation.projects.home.ProjectsCardsRouter
import com.minidashboard.app.presentation.widgets.TemplateScreen
import minidashboard.composeapp.generated.resources.*
import minidashboard.composeapp.generated.resources.Res
import minidashboard.composeapp.generated.resources.monitor_title
import minidashboard.composeapp.generated.resources.projects_create
import minidashboard.composeapp.generated.resources.projects_title
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.jetbrains.compose.resources.stringResource

@Composable
fun Nav(modifier: Modifier = Modifier) {
    val navigator = rememberNavigator()

    NavHost(
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = Route.HOME
    ) {
        scene(
            route = Route.HOME
        ) {
            TemplateScreen(
                title = "App",
                onBackPressed = { navigator.popBackStack() }
            ) {
                HomeScreen(
                    goToMonitor = {},
                    goToProjects = {
                        navigator.navigate(Route.Projects.HOME)
                    },
                    goToSettings = {
                        navigator.navigate(Route.SETTINGS)
                    }
                )
            }
        }
        // regiuon settings
        scene( route = Route.SETTINGS ){
            TemplateScreen(
                title = stringResource(Res.string.settings_title),
                onBackPressed = { navigator.popBackStack() }
            ) {
                DataScreen()
            }
        }
        // endregion
        // region Projects
        scene( route = Route.Projects.HOME ){
            TemplateScreen(
                title = stringResource(Res.string.projects_title),
                onBackPressed = { navigator.popBackStack() }
            ) {
                ProjectScreen(
                    router = object : ProjectsCardsRouter {
                        override fun onTap(project: ProjectModel) {
                            val route = Route.Projects.DASHBOARD.replace("{project}", project.uuid)
                            navigator.navigate(route)
                        }

                        override fun onNewProject() {
                            navigator.navigate(Route.Projects.CREATE)
                        }
                    }
                )
            }
        }
        scene(
            route = Route.Projects.DASHBOARD
        ) { backStackEntry ->
            val project: String = backStackEntry.path<String>("project") ?: return@scene

            TemplateScreen(
                title = "$project/${stringResource(Res.string.monitor_title)}",
                onBackPressed = { navigator.popBackStack() }
            ) {
                MonitorScreen(
                    projectUUID = project,
                    onCreateMonitor = {
                        val route = Route.Projects.Monitor.CREATE
                            .replace("{project}", project)
                            //.replace("{uuid}", "")
                        navigator.navigate(route)
                    },
                    onEditProccess = { selected ->
                        val taskId = selected.task?.uuid ?: "undefined"
                        val route = Route.Projects.Monitor.CREATE
                            .replace("{project}", project)
                            .replace("{uuid}", taskId)
                        navigator.navigate(route)
                    }
                )
            }
        }
        scene(
            route = Route.Projects.CREATE
        ) { backStackEntry ->
            TemplateScreen(
                title = stringResource(Res.string.projects_create),
                onBackPressed = { navigator.popBackStack() }
            ) {
                ProjectCreateScreen(
                    onBackPressed = { navigator.popBackStack() }
                )
            }
        }
        // endregion
        scene(
            route = Route.Projects.Monitor.CREATE
        ) {backStackEntry ->
            val uuid: String? = backStackEntry.path<String>("uuid")
            val project: String = backStackEntry.path<String>("project") ?: return@scene

            TemplateScreen(
                title = "$project/monitor/create/",
                onBackPressed = { navigator.popBackStack() }
            ) {
                CreateMonitorScreen(
                    projectUUID = project,
                    uuid = uuid
                )
            }
        }
    }
}

object Route {
    // SPLASH
    const val SPLASH = "/screen/splash"
    const val HOME = "/screen/home"
    const val SETTINGS = "/screen/settings"

    object Projects {
        const val HOME = "/screen/projects/home"
        const val CREATE = "/screen/projects/create"
        const val DASHBOARD = "/screen/projects/{project}"

        object Monitor {
            const val HOME = "/screen/projects/{project}/monitor"
            const val CREATE = "/screen/projects/{project}/monitor/create"
        }
    }

}