package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.data.dto.ApiException
import com.camphub.app.ui.model.Program
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ProgramFormViewModel : ViewModel() {
    // Data for edit mode; null in add mode
    private val _loadState = MutableStateFlow<UiState<Program?>>(UiState.Success(null))
    val loadState: StateFlow<UiState<Program?>> = _loadState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Non-field errors shown in a Snackbar
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Validation errors per field: "name", "category", "price", "durationWeeks", "syllabus"
    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    // The view watches this and goes back
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun loadProgram(id: Long) {
        viewModelScope.launch {
            _loadState.value = UiState.Loading
            try {
                val program = CampHubServerContainer().bootcampServerRepository.getProgram(id)
                _loadState.value = UiState.Success(program)
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

    // programId == null: create in bootcampId; otherwise update
    fun save(
        bootcampId: Long,
        programId: Long?,
        name: String,
        category: String,
        price: String,
        durationWeeks: String,
        syllabus: String,
        registrationOpen: Boolean
    ) {
        // Price and duration must be numbers before they can be sent
        val priceValue = price.toLongOrNull()
        val durationValue = durationWeeks.toIntOrNull()
        val numberErrors = buildMap {
            if (priceValue == null) put("price", "Harga wajib diisi dengan angka")
            if (durationValue == null) put("durationWeeks", "Durasi wajib diisi dengan angka")
        }
        if (priceValue == null || durationValue == null) {
            _fieldErrors.value = numberErrors
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _fieldErrors.value = emptyMap()
            try {
                val repository = CampHubServerContainer().bootcampServerRepository
                if (programId == null) {
                    repository.createProgram(bootcampId, name, category, priceValue, durationValue, syllabus, registrationOpen)
                } else {
                    repository.updateProgram(programId, name, category, priceValue, durationValue, syllabus, registrationOpen)
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