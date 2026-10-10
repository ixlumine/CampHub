package com.camphub.app.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.model.Review
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.viewmodel.BootcampReviewViewModel

@Composable
fun BootcampReviewSection(
    bootcampId: Long,
    onWriteReview: () -> Unit,
    onEditReview: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BootcampReviewViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var reviewToDelete by remember { mutableStateOf<Review?>(null) }

    LaunchedEffect(bootcampId) {
        viewModel.loadReviews(bootcampId)
    }

    val isUser = CampHubServerContainer.CURRENT_ROLE == "USER"

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Ulasan", style = MaterialTheme.typography.titleLarge)
            if (isUser) {
                FilledTonalButton(onClick = onWriteReview) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tulis Ulasan")
                }
            }
        }

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
            is UiState.Error -> {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is UiState.Success -> {
                val reviews = state.data
                if (reviews.isEmpty()) {
                    Text(
                        text = "Belum ada ulasan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    reviews.forEach { review ->
                        ReviewCard(
                            review = review,
                            onEdit = { onEditReview(review.id) },
                            onDelete = { reviewToDelete = review }
                        )
                    }
                }
            }
        }
    }

    reviewToDelete?.let { review ->
        DeleteDialog(
            title = "Hapus ulasan?",
            message = "Ulasan Anda akan dihapus permanen. Tindakan ini tidak dapat dibatalkan.",
            onConfirm = {
                val id = review.id
                reviewToDelete = null
                viewModel.deleteReview(id, bootcampId)
            },
            onDismiss = { reviewToDelete = null }
        )
    }
}

@Composable
fun ReviewCard(
    review: Review,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOwner = review.authorId == CampHubServerContainer.CURRENT_USER_ID
    val isAdmin = CampHubServerContainer.CURRENT_ROLE == "ADMIN"

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = review.authorName, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingStars(rating = review.rating.toDouble(), starSize = 16.dp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isOwner) {
                        IconButton(onClick = onEdit) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Ubah", modifier = Modifier.size(20.dp))
                        }
                    }
                    if (isOwner || isAdmin) {
                        IconButton(onClick = onDelete) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Hapus", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            if (review.careerStatus == "EMPLOYED") {
                StatusLabel(text = "Diterima Bekerja", icon = Icons.Outlined.WorkOutline)
            } else {
                StatusLabel(text = "Mencari Kerja", icon = Icons.Outlined.Search, highlighted = false)
            }

            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
