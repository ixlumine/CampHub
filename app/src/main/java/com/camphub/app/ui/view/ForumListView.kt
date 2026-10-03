package com.camphub.app.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.ui.model.ForumPost
import com.camphub.app.ui.route.AppView
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.viewmodel.ForumListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumListView(
    onTabSelected: (AppView) -> Unit,
    onLogout: () -> Unit,
    onPostClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    viewModel: ForumListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadPosts() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Forum") },
                actions = { AccountMenu(onLogout = onLogout) }
            )
        },
        bottomBar = { CampHubNavigationBar(selected = AppView.Forum, onTabSelected = onTabSelected) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                text = { Text(text = "Buat Pertanyaan") }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            is UiState.Loading -> LoadingView(modifier = contentModifier)
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadPosts() },
                modifier = contentModifier
            )
            is UiState.Success -> if (state.data.isEmpty()) {
                EmptyView(
                    icon = Icons.Outlined.Forum,
                    message = "Belum ada pertanyaan atau diskusi",
                    modifier = contentModifier
                )
            } else {
                LazyColumn(modifier = contentModifier) {
                    itemsIndexed(state.data) { index, post ->
                        ForumPostItem(
                            post = post,
                            modifier = Modifier.clickable { onPostClick(post.id) }
                        )
                        if (index < state.data.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForumPostItem(post: ForumPost, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(text = post.title, style = MaterialTheme.typography.titleMedium)
        },
        supportingContent = {
            Column(
                modifier = Modifier.padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${post.commentsCount} komentar",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}
