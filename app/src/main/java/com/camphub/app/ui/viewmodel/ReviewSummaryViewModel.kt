package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.model.ReviewSummary
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ReviewSummaryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ReviewSummary>>(UiState.Loading)
    val uiState: StateFlow<UiState<ReviewSummary>> = _uiState

    fun loadSummary(bootcampId: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val summary = CampHubServerContainer().reviewServerRepository.getReviewSummary(bootcampId)
                _uiState.value = UiState.Success(summary)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _uiState.value = UiState.Error("Tidak dapat terhubung ke server")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
