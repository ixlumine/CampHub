package com.camphub.app.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.viewmodel.ReviewFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewFormView(
    bootcampId: Long,
    reviewId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ReviewFormViewModel = viewModel()
) {
    val bootcampState by viewModel.bootcampState.collectAsState()
    val reviewToEdit by viewModel.reviewToEdit.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var rating by rememberSaveable { mutableStateOf(5) }
    var content by rememberSaveable { mutableStateOf("") }
    var careerStatus by rememberSaveable { mutableStateOf("EMPLOYED") }
    var initialized by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bootcampId, reviewId) {
        viewModel.loadForm(bootcampId, reviewId)
    }

    LaunchedEffect(reviewToEdit) {
        reviewToEdit?.let { review ->
            if (!initialized) {
                rating = review.rating
                content = review.content
                careerStatus = review.careerStatus
                initialized = true
            }
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) onSaved()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val isEditMode = reviewId != null
    val title = if (isEditMode) "Ubah Ulasan" else "Tulis Ulasan"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when (val state = bootcampState) {
            is UiState.Loading -> LoadingView(modifier = Modifier.padding(innerPadding))
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadForm(bootcampId, reviewId) },
                modifier = Modifier.padding(innerPadding)
            )
            is UiState.Success -> {
                val bootcamp = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Bootcamp: ${bootcamp.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Rating picker
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Rating", style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "$star Bintang",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { rating = star }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "$rating / 5", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    // Career Status selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Status Karier Alumni", style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = careerStatus == "EMPLOYED",
                                onClick = { careerStatus = "EMPLOYED" },
                                label = { Text("Diterima Bekerja") }
                            )
                            FilterChip(
                                selected = careerStatus == "SEEKING_JOB",
                                onClick = { careerStatus = "SEEKING_JOB" },
                                label = { Text("Mencari Kerja") }
                            )
                        }
                    }

                    // Content Field
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Ulasan Anda") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        isError = fieldErrors.containsKey("content"),
                        supportingText = {
                            if (fieldErrors.containsKey("content")) {
                                Text(text = fieldErrors["content"]!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text(text = "${content.length}/5000 karakter")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.save(
                                bootcampId = bootcampId,
                                reviewId = reviewId,
                                rating = rating,
                                content = content,
                                careerStatus = careerStatus
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && content.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = if (isEditMode) "Simpan Perubahan" else "Kirim Ulasan")
                        }
                    }
                }
            }
        }
    }
}
