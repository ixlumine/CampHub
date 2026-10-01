package com.camphub.app.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.theme.CampHubTheme
import com.camphub.app.ui.viewmodel.BootcampFormViewModel

// Add mode when bootcampId is null, edit mode otherwise
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BootcampFormView(
    bootcampId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: BootcampFormViewModel = viewModel()
) {
    val loadState by viewModel.loadState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var website by rememberSaveable { mutableStateOf("") }
    // Fill the fields only once, so typed text is not overwritten
    var isFilled by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bootcampId) {
        if (bootcampId != null && !isFilled) viewModel.loadBootcamp(bootcampId)
    }

    LaunchedEffect(loadState) {
        val bootcamp = (loadState as? UiState.Success)?.data
        if (bootcamp != null && !isFilled) {
            name = bootcamp.name
            description = bootcamp.description
            location = bootcamp.location
            website = bootcamp.website ?: ""
            isFilled = true
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (bootcampId == null) "Tambah Bootcamp" else "Ubah Bootcamp") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.save(bootcampId, name.trim(), description.trim(), location.trim(), website.trim()) },
                enabled = !isLoading && loadState is UiState.Success,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = "Simpan")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = loadState) {
            is UiState.Loading -> LoadingView(modifier = contentModifier)
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { bootcampId?.let { viewModel.loadBootcamp(it) } },
                modifier = contentModifier
            )
            is UiState.Success -> Column(
                modifier = contentModifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Keys match the backend "details[].field" values
                FormTextField(value = name, onValueChange = { name = it }, label = "Nama bootcamp", error = fieldErrors["name"])
                FormTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Deskripsi",
                    error = fieldErrors["description"],
                    singleLine = false
                )
                FormTextField(value = location, onValueChange = { location = it }, label = "Lokasi", error = fieldErrors["location"])
                FormTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = "Website (opsional)",
                    error = fieldErrors["website"],
                    helper = "Opsional. Contoh: www.namabootcamp.id"
                )
            }
        }
    }
}

// Text field that shows the server error under it
@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    singleLine: Boolean = true,
    helper: String? = null
) {
    val supportingText = error ?: helper
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 5,
        isError = error != null,
        supportingText = supportingText?.let { { Text(text = it) } },
        trailingIcon = if (error != null) {
            { Icon(imageVector = Icons.Filled.Error, contentDescription = null) }
        } else null,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun BootcampFormViewPreview() {
    CampHubTheme {
        BootcampFormView(bootcampId = null, onBack = {}, onSaved = {})
    }
}