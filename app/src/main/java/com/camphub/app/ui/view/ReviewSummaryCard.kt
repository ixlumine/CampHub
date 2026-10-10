package com.camphub.app.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.viewmodel.ReviewSummaryViewModel

@Composable
fun ReviewSummaryCard(
    bootcampId: Long,
    modifier: Modifier = Modifier,
    viewModel: ReviewSummaryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(bootcampId) {
        viewModel.loadSummary(bootcampId)
    }

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        is UiState.Error -> {
            // Keep card unobtrusive if empty or error
        }
        is UiState.Success -> {
            val summary = state.data
            OutlinedCard(
                modifier = modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ringkasan Ulasan",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (summary.reviewCount > 0) String.format("%.1f", summary.averageRating) else "0.0",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            RatingStars(rating = summary.averageRating, starSize = 18.dp)
                            Text(
                                text = "${summary.reviewCount} ulasan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (summary.reviewCount > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Status Karier Alumni",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusLabel(
                                text = "Diterima Bekerja (${summary.employedCount})",
                                icon = Icons.Outlined.WorkOutline
                            )
                            StatusLabel(
                                text = "Mencari Kerja (${summary.seekingJobCount})",
                                icon = Icons.Outlined.Search,
                                highlighted = false
                            )
                        }
                    }
                }
            }
        }
    }
}
