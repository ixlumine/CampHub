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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.camphub.app.ui.state.UiState
import com.camphub.app.ui.viewmodel.ForumFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumFormView(
    postId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ForumFormViewModel = viewModel()
) {
    val loadState by viewModel.loadState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var isFilled by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(postId) {
        if (postId != null && !isFilled) viewModel.loadPost(postId)
    }

    LaunchedEffect(loadState) {
        val post = (loadState as? UiState.Success)?.data
        if (post != null && !isFilled) {
            title = post.title
            content = post.content
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
                title = { Text(text = if (postId == null) "Buat Pertanyaan" else "Ubah Pertanyaan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.save(postId, title.trim(), content.trim()) },
                enabled = !isLoading && loadState is UiState.Success,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = if (postId == null) "Kirim Pertanyaan" else "Simpan")
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
                onRetry = { postId?.let { viewModel.loadPost(it) } },
                modifier = contentModifier
            )
            is UiState.Success -> Column(
                modifier = contentModifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = "Judul") },
                    singleLine = true,
                    isError = fieldErrors["title"] != null,
                    supportingText = fieldErrors["title"]?.let { { Text(text = it) } },
                    trailingIcon = if (fieldErrors["title"] != null) {
                        { Icon(imageVector = Icons.Filled.Error, contentDescription = null) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(text = "Isi") },
                    singleLine = false,
                    minLines = 5,
                    isError = fieldErrors["content"] != null,
                    supportingText = fieldErrors["content"]?.let { { Text(text = it) } },
                    trailingIcon = if (fieldErrors["content"] != null) {
                        { Icon(imageVector = Icons.Filled.Error, contentDescription = null) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
