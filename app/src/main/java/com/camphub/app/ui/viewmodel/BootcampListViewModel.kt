package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.model.Bootcamp
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class BootcampListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Bootcamp>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Bootcamp>>> = _uiState

    fun loadBootcamps() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val bootcamps = CampHubServerContainer().bootcampServerRepository.getAllBootcamps()
                _uiState.value = UiState.Success(bootcamps)
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