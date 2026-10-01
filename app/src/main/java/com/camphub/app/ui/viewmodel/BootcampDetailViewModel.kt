package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.model.Bootcamp
import com.camphub.app.ui.model.Program
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class BootcampDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Bootcamp>>(UiState.Loading)
    val uiState: StateFlow<UiState<Bootcamp>> = _uiState

    private val _programs = MutableStateFlow<List<Program>>(emptyList())
    val programs: StateFlow<List<Program>> = _programs

    fun loadBootcamp(id: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val repository = CampHubServerContainer().bootcampServerRepository
                val bootcamp = repository.getBootcamp(id)
                // Programs are set first, so the screen gets both at once
                _programs.value = repository.getPrograms(id)
                _uiState.value = UiState.Success(bootcamp)
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