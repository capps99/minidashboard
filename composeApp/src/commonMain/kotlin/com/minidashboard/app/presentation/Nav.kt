package com.minidashboard.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.minidashboard.app.presentation.home.HomeScreen
import com.minidashboard.app.presentation.monitor.create.CreateMonitorScreen
import com.minidashboard.app.presentation.monitor.home.MonitorScreen
import com.minidashboard.app.presentation.widgets.TemplateScreen
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

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
                    goToMonitor = {
                        navigator.navigate(Route.Monitor.HOME)
                    }
                )
            }
        }
        scene(
            route = Route.Monitor.HOME
        ) {
            TemplateScreen(
                title = "Monitor",
                onBackPressed = { navigator.popBackStack() }
            ) {
                MonitorScreen(
                    onCreateMonitor = { navigator.navigate(Route.Monitor.CREATE) },
                    onEditProccess = { selected ->
                        navigator.navigate("${Route.Monitor.CREATE}/${selected.cron.common.uuid}")
                    }
                )
            }
        }
        /*scene(
            route = Route.Monitor.CREATE
        ) {
            TemplateScreen(
                title = "Monitor/create",
                onBackPressed = { navigator.popBackStack() }
            ) {
                CreateMonitorScreen()
            }
        }*/
        scene(
            route = "${Route.Monitor.CREATE}/{uuid}?"
        ) {backStackEntry ->
            val uuid: String? = backStackEntry.path<String>("uuid")

            TemplateScreen(
                title = "Monitor/create/",
                onBackPressed = { navigator.popBackStack() }
            ) {
                CreateMonitorScreen(
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

    object Monitor {
        const val HOME = "/screen/monitor/home"
        const val CREATE = "/screen/monitor/create"

        const val EDIT = "/screen/monitor/edit"
    }

}