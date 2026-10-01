package com.camphub.app.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.camphub.app.ui.model.Program
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.theme.CampHubTheme
import com.camphub.app.ui.viewmodel.BootcampDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BootcampDetailView(
    bootcampId: Long,
    onBack: () -> Unit,
    viewModel: BootcampDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val programs by viewModel.programs.collectAsState()

    // Load again whenever this screen is shown
    LaunchedEffect(bootcampId) { viewModel.loadBootcamp(bootcampId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detail Bootcamp") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            is UiState.Loading -> LoadingView(modifier = contentModifier)
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadBootcamp(bootcampId) },
                modifier = contentModifier
            )
            is UiState.Success -> BootcampDetailContent(
                bootcamp = state.data,
                programs = programs,
                modifier = contentModifier
            )
        }
    }
}

// Screen content without the top bar, so it can be previewed
@Composable
private fun BootcampDetailContent(bootcamp: Bootcamp, programs: List<Program>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BootcampHeader(bootcamp = bootcamp) }
        item { Text(text = bootcamp.description, style = MaterialTheme.typography.bodyLarge) }

        // Review summary goes here (above the program list)

        item {
            Text(
                text = "Program",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        if (programs.isEmpty()) {
            item {
                Text(
                    text = "Belum ada program",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(programs) { program -> ProgramCard(program = program) }
        }

        // Review list goes here (below the program list)
    }
}

// Initial, name, location, provider, website
@Composable
private fun BootcampHeader(bootcamp: Bootcamp) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bootcamp.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(text = bootcamp.name, style = MaterialTheme.typography.headlineSmall)
        }
        DetailInfoRow(icon = Icons.Outlined.Place, text = bootcamp.location)
        DetailInfoRow(icon = Icons.Outlined.Business, text = "Penyedia: ${bootcamp.ownerName}")
        // Website is optional
        bootcamp.website?.let { DetailInfoRow(icon = Icons.Outlined.Link, text = it) }
    }
}

// Small icon + text
@Composable
private fun DetailInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BootcampDetailContentPreview() {
    CampHubTheme {
        BootcampDetailContent(
            bootcamp = Bootcamp(
                id = 1,
                name = "Kode Nusantara Academy",
                description = "Bootcamp pengembangan web intensif untuk pemula dan pekerja yang ingin beralih karier.",
                location = "Jakarta Selatan",
                website = "https://kodenusantara.example.com",
                ownerId = 2,
                ownerName = "Kode Nusantara"
            ),
            programs = listOf(
                Program(
                    id = 1,
                    bootcampId = 1,
                    bootcampName = "Kode Nusantara Academy",
                    name = "Full-Stack Web Developer",
                    category = "Web Development",
                    price = 15000000,
                    durationWeeks = 12,
                    syllabus = "",
                    registrationOpen = true
                )
            )
        )
    }
}