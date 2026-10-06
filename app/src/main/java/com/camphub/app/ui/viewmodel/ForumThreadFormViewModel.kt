package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.data.dto.ApiException
import com.camphub.app.ui.model.ForumThread
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ForumThreadFormViewModel : ViewModel() {
    private val _loadState = MutableStateFlow<UiState<ForumThread?>>(UiState.Success(null))
    val loadState: StateFlow<UiState<ForumThread?>> = _loadState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun loadThread(id: Long) {
        viewModelScope.launch {
            _loadState.value = UiState.Loading
            try {
                val thread = CampHubServerContainer().forumServerRepository.getThread(id)
                _loadState.value = UiState.Success(thread)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _loadState.value = UiState.Error("Tidak dapat terhubung ke server")
            } catch (e: Exception) {
                _loadState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun save(id: Long?, title: String, content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _fieldErrors.value = emptyMap()
            try {
                val repo = CampHubServerContainer().forumServerRepository
                if (id == null) {
                    repo.createThread(title = title, content = content)
                } else {
                    repo.updateThread(id = id, title = title, content = content)
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

    fun clearError() {
        _errorMessage.value = null
    }
}
