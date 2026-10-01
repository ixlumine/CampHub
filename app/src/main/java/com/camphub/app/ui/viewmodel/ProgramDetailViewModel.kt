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

    // Owner of the parent bootcamp; program data does not include it
    private val _ownerId = MutableStateFlow<Long?>(null)
    val ownerId: StateFlow<Long?> = _ownerId

    // Delete errors shown in a Snackbar
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // The view watches this and goes back
    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess

    fun loadProgram(id: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val repository = CampHubServerContainer().bootcampServerRepository
                val program = repository.getProgram(id)
                // Owner is set first, so the buttons show together with the data
                _ownerId.value = repository.getBootcamp(program.bootcampId).ownerId
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

    fun deleteProgram(id: Long) {
        viewModelScope.launch {
            try {
                CampHubServerContainer().bootcampServerRepository.deleteProgram(id)
                _deleteSuccess.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _errorMessage.value = "Tidak dapat terhubung ke server"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            }
        }
    }

    // Called by the View after the Snackbar is shown
    fun clearError() {
        _errorMessage.value = null
    }
}