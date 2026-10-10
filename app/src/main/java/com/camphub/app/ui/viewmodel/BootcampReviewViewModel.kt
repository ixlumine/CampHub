package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.data.dto.ApiException
import com.camphub.app.ui.model.Review
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class BootcampReviewViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Review>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Review>>> = _uiState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadReviews(bootcampId: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val reviews = CampHubServerContainer().reviewServerRepository.getReviews(bootcampId)
                _uiState.value = UiState.Success(reviews)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _uiState.value = UiState.Error("Tidak dapat terhubung ke server")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun deleteReview(id: Long, bootcampId: Long) {
        viewModelScope.launch {
            try {
                CampHubServerContainer().reviewServerRepository.deleteReview(id)
                loadReviews(bootcampId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                _errorMessage.value = e.message ?: "Gagal menghapus ulasan"
            } catch (e: IOException) {
                _errorMessage.value = "Tidak dapat terhubung ke server"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
