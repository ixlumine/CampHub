package com.camphub.app.ui.state

// Screen state used by every ViewModel (Sesi 9 module)
sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}