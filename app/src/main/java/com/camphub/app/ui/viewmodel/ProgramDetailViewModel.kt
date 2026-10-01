package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.model.Program
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ProgramDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Program>>(UiState.Loading)
    val uiState: StateFlow<UiState<Program>> = _uiState

    fun loadProgram(id: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val program = CampHubServerContainer().bootcampServerRepository.getProgram(id)
                _uiState.value = UiState.Success(program)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                // Network failure: backend off, timeout, no connection
                _uiState.value = UiState.Error("Tidak dapat terhubung ke server")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}