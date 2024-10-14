package com.itsecurity.video.chat.agora

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itsecurity.video.chat.agora.model.Component
import com.itsecurity.video.chat.agora.model.Components
import com.itsecurity.video.chat.agora.model.Subpage
import com.itsecurity.video.chat.agora.model.UiStore
import com.itsecurity.video.chat.agora.ui.home.Home
import com.itsecurity.video.chat.agora.ui.page.Subpage
import com.itsecurity.video.chat.agora.ui.settings.Settings

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable(HomeRoute) {
            Home(
                components = Components,
                onSubpageClick = { subpage, component ->
                    navController.navigate(
                        subpage.route(
                            component
                        )
                    )
                },
                onSettingClick = { navController.navigate(SettingsRoute) }
            )
        }
        composable(SettingsRoute) {
            Settings {
                navController.popBackStack()
            }
        }
        composable(
            route = "$SubpageRoute/" +
                    "{$ComponentIdArgName}/" +
                    "{$SubpageIndexArgName}",
            arguments = listOf(
                navArgument(ComponentIdArgName) { type = NavType.IntType },
                navArgument(SubpageIndexArgName) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val arguments = requireNotNull(navBackStackEntry.arguments) { "No arguments" }
            val componentId = arguments.getInt(ComponentIdArgName)
            val subpageIndex = arguments.getInt(SubpageIndexArgName)
            val component = Components.first { component -> component.id == componentId }
            val subpage = component.subpages[subpageIndex]
            Subpage(
                subPage = subpage,
                onBackClick = {
                    UiStore.stopRecording()
                    navController.popBackStack()
                },
            )
        }
    }

}

private fun Subpage.route(component: Component) =
    "$SubpageRoute/${component.id}/${component.subpages.indexOf(this)}"

private const val HomeRoute = "home"
private const val SettingsRoute = "settings"
private const val SubpageRoute = "subpage"
private const val ComponentIdArgName = "componentId"
private const val SubpageIndexArgName = "subpageIndex"