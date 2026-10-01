package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.data.dto.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import java.io.IOException

class RegisterViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Non-field errors (e.g. "Email sudah terdaftar") shown in a Snackbar
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Validation errors per field: "name", "email", "password" (mockup Register)
    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    // Register response already has a token, so the user is logged in directly (spec 6.5)
    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _fieldErrors.value = emptyMap()
            try {
                val auth = CampHubServerContainer().authServerRepository.register(name, email, password)
                CampHubServerContainer.ACCESS_TOKEN = auth.token
                CampHubServerContainer.CURRENT_USER_ID = auth.userId
                CampHubServerContainer.CURRENT_ROLE = auth.role
                CampHubServerContainer.CURRENT_NAME = auth.name
                _registerSuccess.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                if (e.fieldErrors.isNotEmpty()) {
                    _fieldErrors.value = e.fieldErrors
                } else {
                    _errorMessage.value = e.message ?: "Terjadi kesalahan"
                }
            } catch (e: IOException) {
                // Network failure: backend off, timeout, no connection
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