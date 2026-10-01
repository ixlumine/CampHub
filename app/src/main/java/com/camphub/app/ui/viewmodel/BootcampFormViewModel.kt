package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.data.dto.ApiException
import com.camphub.app.ui.model.Bootcamp
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class BootcampFormViewModel : ViewModel() {
    // Data for edit mode; null in add mode
    private val _loadState = MutableStateFlow<UiState<Bootcamp?>>(UiState.Success(null))
    val loadState: StateFlow<UiState<Bootcamp?>> = _loadState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Non-field errors shown in a Snackbar
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Validation errors per field: "name", "description", "location", "website"
    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    // The view watches this and goes back
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun loadBootcamp(id: Long) {
        viewModelScope.launch {
            _loadState.value = UiState.Loading
            try {
                val bootcamp = CampHubServerContainer().bootcampServerRepository.getBootcamp(id)
                _loadState.value = UiState.Success(bootcamp)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                // Network failure: backend off, timeout, no connection
                _loadState.value = UiState.Error("Tidak dapat terhubung ke server")
            } catch (e: Exception) {
                _loadState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    // id == null: create; otherwise update
    fun save(id: Long?, name: String, description: String, location: String, website: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _fieldErrors.value = emptyMap()
            // Empty website is sent as null
            val websiteOrNull = website.ifBlank { null }
            try {
                val repository = CampHubServerContainer().bootcampServerRepository
                if (id == null) {
                    repository.createBootcamp(name, description, location, websiteOrNull)
                } else {
                    repository.updateBootcamp(id, name, description, location, websiteOrNull)
                }
                _saveSuccess.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                if (e.fieldErrors.isNotEmpty()) {
                    _fieldErrors.value = e.fieldErrors
                } else {
                    _errorMessage.value = e.message ?: "Terjadi kesalahan"
                }
            } catch (e: IOException) {
                _errorMessage.value = "Tidak dapat terhubung ke server"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Called by the View after the Snackbar is shown
    fun clearError() {
        _errorMessage.value = null
    }
}