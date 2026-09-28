package com.camphub.app.ui.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.view.LoginView
import com.camphub.app.ui.view.RegisterView

// Each developer adds their routes here (spec 6.2, rule 3)
enum class AppView {
    Login,
    Register,
    Ranking
}

@Composable
fun AppRouting() {
    val navController = rememberNavController()

    // After login/register: open the main screen and remove Login/Register from history (spec 6.5)
    val openMain: () -> Unit = {
        navController.navigate(AppView.Ranking.name) {
            popUpTo(AppView.Login.name) { inclusive = true }
            launchSingleTop = true
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
        composable(AppView.Ranking.name) {
            // Temporary screen to verify login; replaced by the three tabs in A5
            Scaffold { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Masuk sebagai userId ${CampHubServerContainer.CURRENT_USER_ID}, role ${CampHubServerContainer.CURRENT_ROLE}"
                    )
                }
            }
        }
    }
}