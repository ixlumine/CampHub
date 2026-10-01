package com.camphub.app.ui.route

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.view.BootcampDetailView
import com.camphub.app.ui.view.BootcampListView
import com.camphub.app.ui.view.ForumListView
import com.camphub.app.ui.view.LoginView
import com.camphub.app.ui.view.RankingView
import com.camphub.app.ui.view.RegisterView

// Add new routes here
enum class AppView {
    Login,
    Register,
    Ranking,
    Catalog,
    Forum,
    BootcampDetail
}

@Composable
fun AppRouting() {
    val navController = rememberNavController()

    // After login or register: open the first tab and remove Login/Register from history
    val openMain: () -> Unit = {
        navController.navigate(AppView.Ranking.name) {
            popUpTo(AppView.Login.name) { inclusive = true }
            launchSingleTop = true
        }
    }

    // Switch tabs: keep one copy of each tab; Back returns to the first tab
    val openTab: (AppView) -> Unit = { tab ->
        navController.navigate(tab.name) {
            popUpTo(AppView.Ranking.name) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Logout: clear the session and history, then show Login
    val logout: () -> Unit = {
        CampHubServerContainer.ACCESS_TOKEN = ""
        CampHubServerContainer.CURRENT_USER_ID = -1
        CampHubServerContainer.CURRENT_ROLE = ""
        CampHubServerContainer.CURRENT_NAME = ""
        navController.navigate(AppView.Login.name) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = AppView.Login.name) {
        composable(AppView.Login.name) {
            LoginView(
                onLoginSuccess = openMain,
                onRegisterClick = { navController.navigate(AppView.Register.name) }
            )
        }
        composable(AppView.Register.name) {
            RegisterView(
                onRegisterSuccess = openMain,
                onBack = { navController.popBackStack() }
            )
        }

        // Tabs
        composable(AppView.Ranking.name) {
            RankingView(onTabSelected = openTab, onLogout = logout)
        }
        composable(AppView.Catalog.name) {
            BootcampListView(
                onTabSelected = openTab,
                onLogout = logout,
                onBootcampClick = { id -> navController.navigate("${AppView.BootcampDetail.name}/$id") }
            )
        }
        composable(AppView.Forum.name) {
            ForumListView(onTabSelected = openTab, onLogout = logout)
        }

        // Catalog
        composable("${AppView.BootcampDetail.name}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")!!.toLong()
            BootcampDetailView(bootcampId = id, onBack = { navController.popBackStack() })
        }
    }
}