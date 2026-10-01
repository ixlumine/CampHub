package com.camphub.app.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.camphub.app.ui.model.Program
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.theme.CampHubTheme
import com.camphub.app.ui.viewmodel.ProgramDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailView(
    programId: Long,
    onBack: () -> Unit,
    onEdit: (bootcampId: Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: ProgramDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val ownerId by viewModel.ownerId.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Load again whenever this screen is shown
    LaunchedEffect(programId) { viewModel.loadProgram(programId) }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) onDeleted()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Buttons by role and owner; the backend also checks this
    val program = (uiState as? UiState.Success)?.data
    val isOwner = program != null && ownerId == CampHubServerContainer.CURRENT_USER_ID
    val isAdmin = program != null && CampHubServerContainer.CURRENT_ROLE == "ADMIN"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detail Program") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (program != null && isOwner) {
                        IconButton(onClick = { onEdit(program.bootcampId) }) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Ubah")
                        }
                    }
                    if (isOwner || isAdmin) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Hapus")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            is UiState.Loading -> LoadingView(modifier = contentModifier)
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadProgram(programId) },
                modifier = contentModifier
            )
            is UiState.Success -> ProgramDetailContent(program = state.data, modifier = contentModifier)
        }
    }

    if (showDeleteDialog && program != null) {
        DeleteDialog(
            title = "Hapus program?",
            message = "${program.name} akan dihapus permanen. Tindakan ini tidak dapat dibatalkan.",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteProgram(program.id)
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

// Screen content without the top bar, so it can be previewed
@Composable
private fun ProgramDetailContent(program: Program, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = program.category,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = program.name, style = MaterialTheme.typography.headlineSmall)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                imageVector = Icons.Outlined.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = program.bootcampName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Price and duration side by side
        Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            LabeledValue(label = "Harga", value = formatRupiah(program.price))
            LabeledValue(label = "Durasi", value = "${program.durationWeeks} minggu")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Pendaftaran",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (program.registrationOpen) {
                StatusLabel(text = "Dibuka", icon = Icons.Outlined.CheckCircle)
            } else {
                StatusLabel(text = "Ditutup", icon = Icons.Outlined.Block, highlighted = false)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Silabus", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = program.syllabus, style = MaterialTheme.typography.bodyLarge)
    }
}

// Small label above a value
@Composable
private fun LabeledValue(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgramDetailContentPreview() {
    CampHubTheme {
        ProgramDetailContent(
            program = Program(
                id = 1,
                bootcampId = 1,
                bootcampName = "Kode Nusantara Academy",
                name = "Full-Stack Web Developer",
                category = "Web Development",
                price = 15000000,
                durationWeeks = 12,
                syllabus = "Minggu 1–2: Dasar HTML, CSS, dan JavaScript\nMinggu 3–4: Git dan kolaborasi tim",
                registrationOpen = true
            )
        )
    }
}