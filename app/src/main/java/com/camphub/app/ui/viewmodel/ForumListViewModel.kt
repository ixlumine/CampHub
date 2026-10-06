package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.model.ForumThread
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ForumListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<ForumThread>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ForumThread>>> = _uiState

    fun loadThreads() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val threads = CampHubServerContainer().forumServerRepository.getAllThreads()
                _uiState.value = UiState.Success(threads)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _uiState.value = UiState.Error("Tidak dapat terhubung ke server")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
