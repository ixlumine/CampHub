package com.camphub.app.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.theme.CampHubTheme
import com.camphub.app.ui.viewmodel.ProgramFormViewModel

// Add mode when programId is null, edit mode otherwise
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramFormView(
    bootcampId: Long,
    programId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProgramFormViewModel = viewModel()
) {
    val loadState by viewModel.loadState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var durationWeeks by rememberSaveable { mutableStateOf("") }
    var syllabus by rememberSaveable { mutableStateOf("") }
    var registrationOpen by rememberSaveable { mutableStateOf(true) }
    // Fill the fields only once, so typed text is not overwritten
    var isFilled by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(programId) {
        if (programId != null && !isFilled) viewModel.loadProgram(programId)
    }

    LaunchedEffect(loadState) {
        val program = (loadState as? UiState.Success)?.data
        if (program != null && !isFilled) {
            name = program.name
            category = program.category
            price = program.price.toString()
            durationWeeks = program.durationWeeks.toString()
            syllabus = program.syllabus
            registrationOpen = program.registrationOpen
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
                title = { Text(text = if (programId == null) "Tambah Program" else "Ubah Program") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.save(
                        bootcampId = bootcampId,
                        programId = programId,
                        name = name.trim(),
                        category = category.trim(),
                        price = price,
                        durationWeeks = durationWeeks,
                        syllabus = syllabus.trim(),
                        registrationOpen = registrationOpen
                    )
                },
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
                onRetry = { programId?.let { viewModel.loadProgram(it) } },
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
                ProgramTextField(value = name, onValueChange = { name = it }, label = "Nama program", error = fieldErrors["name"])
                ProgramTextField(value = category, onValueChange = { category = it }, label = "Kategori", error = fieldErrors["category"])
                // Digits only
                ProgramTextField(
                    value = price,
                    onValueChange = { price = it.filter { char -> char.isDigit() } },
                    label = "Harga",
                    error = fieldErrors["price"],
                    prefix = "Rp ",
                    isNumber = true
                )
                ProgramTextField(
                    value = durationWeeks,
                    onValueChange = { durationWeeks = it.filter { char -> char.isDigit() } },
                    label = "Durasi",
                    error = fieldErrors["durationWeeks"],
                    suffix = "minggu",
                    isNumber = true
                )
                ProgramTextField(
                    value = syllabus,
                    onValueChange = { syllabus = it },
                    label = "Silabus",
                    error = fieldErrors["syllabus"],
                    singleLine = false
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Pendaftaran dibuka", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = registrationOpen,
                        onCheckedChange = { registrationOpen = it },
                        thumbContent = if (registrationOpen) {
                            { Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize)) }
                        } else null
                    )
                }
            }
        }
    }
}

// Text field that shows the server error under it
@Composable
private fun ProgramTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    singleLine: Boolean = true,
    prefix: String? = null,
    suffix: String? = null,
    isNumber: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 6,
        prefix = prefix?.let { { Text(text = it) } },
        suffix = suffix?.let { { Text(text = it) } },
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        isError = error != null,
        supportingText = error?.let { { Text(text = it) } },
        trailingIcon = if (error != null) {
            { Icon(imageVector = Icons.Filled.Error, contentDescription = null) }
        } else null,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun ProgramFormViewPreview() {
    CampHubTheme {
        ProgramFormView(bootcampId = 1, programId = null, onBack = {}, onSaved = {})
    }
}