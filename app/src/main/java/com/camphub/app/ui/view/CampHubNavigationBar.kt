package com.camphub.app.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.camphub.app.ui.route.AppView

// Bottom navigation for the three tabs (spec 6.5)
@Composable
fun CampHubNavigationBar(selected: AppView, onTabSelected: (AppView) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selected == AppView.Ranking,
            onClick = { onTabSelected(AppView.Ranking) },
            icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = null) },
            label = { Text(text = "Beranda") }
        )
        NavigationBarItem(
            selected = selected == AppView.Catalog,
            onClick = { onTabSelected(AppView.Catalog) },
            icon = { Icon(imageVector = Icons.Outlined.Explore, contentDescription = null) },
            label = { Text(text = "Katalog") }
        )
        NavigationBarItem(
            selected = selected == AppView.Forum,
            onClick = { onTabSelected(AppView.Forum) },
            icon = { Icon(imageVector = Icons.Outlined.Forum, contentDescription = null) },
            label = { Text(text = "Forum") }
        )
    }
}