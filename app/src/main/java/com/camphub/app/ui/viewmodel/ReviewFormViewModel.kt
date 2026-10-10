package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.data.dto.ApiException
import com.camphub.app.ui.model.Bootcamp
import com.camphub.app.ui.model.Review
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ReviewFormViewModel : ViewModel() {
    private val _bootcampState = MutableStateFlow<UiState<Bootcamp>>(UiState.Loading)
    val bootcampState: StateFlow<UiState<Bootcamp>> = _bootcampState

    private val _reviewToEdit = MutableStateFlow<Review?>(null)
    val reviewToEdit: StateFlow<Review?> = _reviewToEdit

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun loadForm(bootcampId: Long, reviewId: Long?) {
        viewModelScope.launch {
            _bootcampState.value = UiState.Loading
            try {
                val bootcamp = CampHubServerContainer().bootcampServerRepository.getBootcamp(bootcampId)
                _bootcampState.value = UiState.Success(bootcamp)

                if (reviewId != null) {
                    val reviews = CampHubServerContainer().reviewServerRepository.getReviews(bootcampId)
                    _reviewToEdit.value = reviews.find { it.id == reviewId }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _bootcampState.value = UiState.Error("Tidak dapat terhubung ke server")
            } catch (e: Exception) {
                _bootcampState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun save(bootcampId: Long, reviewId: Long?, rating: Int, content: String, careerStatus: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _fieldErrors.value = emptyMap()
            try {
                val repository = CampHubServerContainer().reviewServerRepository
                if (reviewId == null) {
                    repository.createReview(bootcampId, rating, content.trim(), careerStatus)
                } else {
                    repository.updateReview(reviewId, rating, content.trim(), careerStatus)
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
