package com.camphub.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camphub.app.data.container.CampHubServerContainer
import com.camphub.app.ui.model.ForumComment
import com.camphub.app.ui.model.ForumThread
import com.camphub.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ForumThreadDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ForumThread>>(UiState.Loading)
    val uiState: StateFlow<UiState<ForumThread>> = _uiState

    private val _comments = MutableStateFlow<List<ForumComment>>(emptyList())
    val comments: StateFlow<List<ForumComment>> = _comments

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess

    private val _isSubmittingComment = MutableStateFlow(false)
    val isSubmittingComment: StateFlow<Boolean> = _isSubmittingComment

    fun loadThreadDetail(id: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val repo = CampHubServerContainer().forumServerRepository
                val thread = repo.getThread(id)
                val commentList = repo.getComments(id)
                _comments.value = commentList
                _uiState.value = UiState.Success(thread)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _uiState.value = UiState.Error("Tidak dapat terhubung ke server")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun deleteThread(id: Long) {
        viewModelScope.launch {
            try {
                CampHubServerContainer().forumServerRepository.deleteThread(id)
                _deleteSuccess.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _errorMessage.value = "Tidak dapat terhubung ke server"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            }
        }
    }

    fun addComment(threadId: Long, content: String, onCommentAdded: () -> Unit) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _isSubmittingComment.value = true
            try {
                CampHubServerContainer().forumServerRepository.addComment(threadId, content)
                onCommentAdded()
                loadThreadDetail(threadId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _errorMessage.value = "Tidak dapat terhubung ke server"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isSubmittingComment.value = false
            }
        }
    }

    fun deleteComment(commentId: Long, threadId: Long) {
        viewModelScope.launch {
            try {
                CampHubServerContainer().forumServerRepository.deleteComment(commentId)
                loadThreadDetail(threadId)
            } catch (e: CancellationException) {
                throw e
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
