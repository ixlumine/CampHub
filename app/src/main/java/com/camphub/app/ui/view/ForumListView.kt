package com.camphub.app.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.route.AppView
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.viewmodel.ForumListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumListView(
    onTabSelected: (AppView) -> Unit,
    onLogout: () -> Unit,
    onThreadClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    viewModel: ForumListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val canCreateThread = CampHubServerContainer.CURRENT_ROLE == "USER" || CampHubServerContainer.CURRENT_ROLE == "PROVIDER"

    LaunchedEffect(Unit) { viewModel.loadThreads() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Forum") },
                actions = { AccountMenu(onLogout = onLogout) }
            )
        },
        bottomBar = { CampHubNavigationBar(selected = AppView.Forum, onTabSelected = onTabSelected) },
        floatingActionButton = {
            if (canCreateThread) {
                ExtendedFloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                    text = { Text(text = "Buat Pertanyaan") }
                )
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            is UiState.Loading -> LoadingView(modifier = contentModifier)
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadThreads() },
                modifier = contentModifier
            )
            is UiState.Success -> if (state.data.isEmpty()) {
                EmptyView(
                    icon = Icons.Outlined.Forum,
                    message = "Belum ada pertanyaan atau diskusi",
                    modifier = contentModifier
                )
            } else {
                LazyColumn(
                    modifier = contentModifier,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data) { thread ->
                        ForumThreadCard(
                            thread = thread,
                            onClick = { onThreadClick(thread.id) }
                        )
                    }
                }
            }
        }
    }
}
