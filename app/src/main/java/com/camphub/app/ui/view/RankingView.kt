package com.camphub.app.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.ui.model.Ranking
import com.camphub.app.ui.route.AppView
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.viewmodel.RankingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingView(
    onTabSelected: (AppView) -> Unit,
    onLogout: () -> Unit,
    onBootcampClick: (Long) -> Unit,
    viewModel: RankingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRankings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Peringkat Bootcamp") },
                actions = { AccountMenu(onLogout = onLogout) }
            )
        },
        bottomBar = {
            CampHubNavigationBar(selected = AppView.Ranking, onTabSelected = onTabSelected)
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            is UiState.Loading -> LoadingView(modifier = contentModifier)
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadRankings() },
                modifier = contentModifier
            )
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyView(
                        icon = Icons.Outlined.EmojiEvents,
                        message = "Belum ada bootcamp yang memenuhi syarat peringkat (minimal 3 ulasan)",
                        modifier = contentModifier
                    )
                } else {
                    LazyColumn(
                        modifier = contentModifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(state.data) { _, ranking ->
                            RankingCard(
                                ranking = ranking,
                                onClick = { onBootcampClick(ranking.bootcampId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingCard(
    ranking: Ranking,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rank badge
            val badgeColor = when (ranking.rank) {
                1 -> MaterialTheme.colorScheme.primary
                2 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.outline
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${ranking.rank}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.surface
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = ranking.bootcampName,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = ranking.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RatingStars(rating = ranking.averageRating, starSize = 16.dp)
                    Text(
                        text = "${String.format("%.1f", ranking.averageRating)} (${ranking.reviewCount} ulasan)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}