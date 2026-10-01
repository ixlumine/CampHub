package com.camphub.app.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.ui.model.Bootcamp
import com.camphub.app.ui.route.AppView
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.theme.CampHubTheme
import com.camphub.app.ui.viewmodel.BootcampListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BootcampListView(
    onTabSelected: (AppView) -> Unit,
    onLogout: () -> Unit,
    onBootcampClick: (Long) -> Unit,
    viewModel: BootcampListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load each time the tab is opened, so new data shows up
    LaunchedEffect(Unit) { viewModel.loadBootcamps() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Katalog Bootcamp") },
                actions = { AccountMenu(onLogout = onLogout) }
            )
        },
        bottomBar = { CampHubNavigationBar(selected = AppView.Catalog, onTabSelected = onTabSelected) }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            is UiState.Loading -> LoadingView(modifier = contentModifier)
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadBootcamps() },
                modifier = contentModifier
            )
            is UiState.Success -> if (state.data.isEmpty()) {
                EmptyView(icon = Icons.Outlined.Explore, message = "Belum ada bootcamp", modifier = contentModifier)
            } else {
                LazyColumn(modifier = contentModifier) {
                    itemsIndexed(state.data) { index, bootcamp ->
                        BootcampListItem(
                            bootcamp = bootcamp,
                            modifier = Modifier.clickable { onBootcampClick(bootcamp.id) }
                        )
                        // Divider between items, aligned with the text
                        if (index < state.data.lastIndex) {
                            HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
                        }
                    }
                }
            }
        }
    }
}

// One row: initial, name, location, provider
@Composable
private fun BootcampListItem(bootcamp: Bootcamp, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(text = bootcamp.name, style = MaterialTheme.typography.titleMedium)
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                InfoRow(icon = Icons.Outlined.Place, text = bootcamp.location)
                InfoRow(icon = Icons.Outlined.Business, text = bootcamp.ownerName)
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bootcamp.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        modifier = modifier
    )
}

// Small icon + text
@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun BootcampListItemPreview() {
    CampHubTheme {
        BootcampListItem(
            bootcamp = Bootcamp(
                id = 1,
                name = "Kode Nusantara Academy",
                description = "",
                location = "Jakarta Selatan",
                website = null,
                ownerId = 2,
                ownerName = "Kode Nusantara"
            )
        )
    }
}