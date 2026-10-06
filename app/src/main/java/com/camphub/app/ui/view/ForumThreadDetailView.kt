package com.camphub.app.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.model.ForumComment
import com.camphub.app.ui.model.ForumThread
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.theme.CampHubTheme
import com.camphub.app.ui.viewmodel.ForumThreadDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumThreadDetailView(
    threadId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: ForumThreadDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()
    val isSubmittingComment by viewModel.isSubmittingComment.collectAsState()

    var showDeleteThreadDialog by rememberSaveable { mutableStateOf(false) }
    var commentToDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }
    var newCommentText by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(threadId) { viewModel.loadThreadDetail(threadId) }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) onDeleted()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val thread = (uiState as? UiState.Success)?.data
    val currentUserId = CampHubServerContainer.CURRENT_USER_ID
    val currentRole = CampHubServerContainer.CURRENT_ROLE
    val isAuthor = thread?.authorId == currentUserId
    val isAdmin = currentRole == "ADMIN"
    val canComment = currentRole == "USER" || currentRole == "PROVIDER"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Diskusi") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (isAuthor) {
                        IconButton(onClick = onEdit) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Ubah")
                        }
                    }
                    if (isAuthor || isAdmin) {
                        IconButton(onClick = { showDeleteThreadDialog = true }) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Hapus")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState is UiState.Success && canComment) {
                Surface(tonalElevation = 3.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = { Text("Tulis komentar...") },
                            modifier = Modifier.weight(1f),
                            singleLine = false,
                            maxLines = 3
                        )
                        IconButton(
                            onClick = {
                                viewModel.addComment(threadId, newCommentText) {
                                    newCommentText = ""
                                }
                            },
                            enabled = newCommentText.isNotBlank() && !isSubmittingComment
                        ) {
                            if (isSubmittingComment) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Kirim Komentar",
                                    tint = if (newCommentText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            is UiState.Loading -> LoadingView(modifier = contentModifier)
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadThreadDetail(threadId) },
                modifier = contentModifier
            )
            is UiState.Success -> ForumThreadDetailContent(
                thread = state.data,
                comments = comments,
                currentUserId = currentUserId,
                isAdmin = isAdmin,
                onDeleteComment = { commentId -> commentToDeleteId = commentId },
                modifier = contentModifier
            )
        }
    }

    if (showDeleteThreadDialog && thread != null) {
        DeleteDialog(
            title = "Hapus pertanyaan?",
            message = "Diskusi ini akan dihapus permanen beserta semua komentarnya. Tindakan ini tidak dapat dibatalkan.",
            onConfirm = {
                showDeleteThreadDialog = false
                viewModel.deleteThread(thread.id)
            },
            onDismiss = { showDeleteThreadDialog = false }
        )
    }

    if (commentToDeleteId != null) {
        DeleteDialog(
            title = "Hapus komentar?",
            message = "Komentar ini akan dihapus permanen.",
            onConfirm = {
                val cId = commentToDeleteId!!
                commentToDeleteId = null
                viewModel.deleteComment(cId, threadId)
            },
            onDismiss = { commentToDeleteId = null }
        )
    }
}

@Composable
private fun ForumThreadDetailContent(
    thread: ForumThread,
    comments: List<ForumComment>,
    currentUserId: Long,
    isAdmin: Boolean,
    onDeleteComment: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = thread.title, style = MaterialTheme.typography.headlineSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = thread.authorName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    val dateFormatted = formatDate(thread.createdAt)
                    val authorText = if (dateFormatted.isNotBlank()) "${thread.authorName} · $dateFormatted" else thread.authorName
                    Text(
                        text = authorText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(text = thread.content, style = MaterialTheme.typography.bodyLarge)
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider()
                Text(
                    text = "Komentar (${comments.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        if (comments.isEmpty()) {
            item {
                Text(
                    text = "Belum ada komentar. Jadilah yang pertama memberikan respon!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(comments) { comment ->
                // Comment can only be deleted by comment author or ADMIN
                val canDeleteComment = comment.authorId == currentUserId || isAdmin
                val commentDate = formatDate(comment.createdAt)
                ListItem(
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color = MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = comment.authorName.take(1).uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    },
                    headlineContent = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = comment.authorName, style = MaterialTheme.typography.labelLarge)
                            if (commentDate.isNotBlank()) {
                                Text(
                                    text = commentDate,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    supportingContent = {
                        Text(
                            text = comment.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    trailingContent = {
                        if (canDeleteComment) {
                            IconButton(onClick = { onDeleteComment(comment.id) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Hapus Komentar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForumThreadDetailContentPreview() {
    CampHubTheme {
        ForumThreadDetailContent(
            thread = ForumThread(
                id = 1,
                authorId = 2,
                authorName = "Andi Saputra",
                title = "Apakah ada kelas malam untuk karyawan?",
                content = "Saya bekerja sampai pukul 17.00 di hari kerja. Apakah program Full-Stack Web Developer punya jadwal kelas malam atau akhir pekan?",
                commentCount = 2,
                createdAt = "2026-09-20T19:00:00"
            ),
            comments = listOf(
                ForumComment(
                    id = 1,
                    threadId = 1,
                    authorId = 3,
                    authorName = "Kode Nusantara",
                    content = "Ada. Kelas malam berjalan Senin–Kamis pukul 19.00–21.30 WIB secara daring.",
                    createdAt = "2026-09-20T21:00:00"
                ),
                ForumComment(
                    id = 2,
                    threadId = 1,
                    authorId = 4,
                    authorName = "Rina Wulandari",
                    content = "Saya ikut kelas malam angkatan lalu. Jadwalnya cocok untuk yang bekerja.",
                    createdAt = "2026-09-21T08:00:00"
                )
            ),
            currentUserId = 4,
            isAdmin = false,
            onDeleteComment = {}
        )
    }
}
