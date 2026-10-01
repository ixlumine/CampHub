package com.camphub.app.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.camphub.app.ui.route.AppView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumListView(onTabSelected: (AppView) -> Unit, onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Forum") },
                actions = { AccountMenu(onLogout = onLogout) }
            )
        },
        bottomBar = { CampHubNavigationBar(selected = AppView.Forum, onTabSelected = onTabSelected) }
    ) { innerPadding ->
        // Placeholder content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Forum (dikerjakan Dev 3)")
        }
    }
}