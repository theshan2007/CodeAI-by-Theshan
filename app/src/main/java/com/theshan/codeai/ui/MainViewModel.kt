package com.theshan.codeai.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theshan.codeai.data.network.NetworkModule
import com.theshan.codeai.data.repository.AiResult
import com.theshan.codeai.data.repository.GeminiRepository
import kotlinx.coroutines.launch

data class ChatMessage(
    val userMessage: String,
    val aiResponse: String? = null,
    val isLoading: Boolean = false,
    val hasCode: Boolean = false,
    val fullCode: String? = null
)

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String, val hasCode: Boolean, val fullCode: String?) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel : ViewModel() {

    private val repository = GeminiRepository(NetworkModule.geminiApiService)

    private val _chatMessages = MutableLiveData<List<ChatMessage>>(emptyList())
    val chatMessages: LiveData<List<ChatMessage>> = _chatMessages

    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> = _uiState

    var currentMode: String = "android"

    fun sendMessage(apiKey: String, userPrompt: String) {
        if (userPrompt.isBlank()) return

        // Add user message immediately
        val userMsg = ChatMessage(userMessage = userPrompt, isLoading = true)
        val currentList = _chatMessages.value?.toMutableList() ?: mutableListOf()
        currentList.add(userMsg)
        _chatMessages.value = currentList
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val result = repository.generateCode(
                apiKey = apiKey,
                userPrompt = userPrompt,
                mode = currentMode
            )

            val updatedList = _chatMessages.value?.toMutableList() ?: mutableListOf()
            val lastIndex = updatedList.lastIndex

            when (result) {
                is AiResult.Success -> {
                    val hasCode = result.text.contains("```") ||
                            result.text.contains("fun ") ||
                            result.text.contains("class ") ||
                            result.text.contains("<html") ||
                            result.text.length > 500

                    val summary = if (hasCode && result.text.length > 400) {
                        extractSummary(result.text) + "\n\n✅ Full code generated! Tap 'View Full Code' to see it."
                    } else {
                        result.text
                    }

                    updatedList[lastIndex] = ChatMessage(
                        userMessage = userPrompt,
                        aiResponse = summary,
                        isLoading = false,
                        hasCode = hasCode,
                        fullCode = if (hasCode) result.text else null
                    )
                    _chatMessages.value = updatedList
                    _uiState.value = UiState.Success(summary, hasCode, if (hasCode) result.text else null)
                }

                is AiResult.Error -> {
                    updatedList[lastIndex] = ChatMessage(
                        userMessage = userPrompt,
                        aiResponse = "❌ ${result.message}",
                        isLoading = false
                    )
                    _chatMessages.value = updatedList
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }

    private fun extractSummary(fullText: String): String {
        val lines = fullText.lines()
        val summaryLines = lines.take(6).filter { it.isNotBlank() }
        return summaryLines.joinToString("\n")
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
        _uiState.value = UiState.Idle
    }
}
