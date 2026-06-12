package com.example.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.RetrofitClient
import com.example.data.local.AppDatabase
import com.example.data.local.TranslationEntity
import com.example.data.repository.TranslationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TranslationViewModel(
    private val repository: TranslationRepository
) : ViewModel() {

    private val _sourceLanguage = MutableStateFlow("Auto-detect")
    val sourceLanguage: StateFlow<String> = _sourceLanguage.asStateFlow()

    private val _targetLanguage = MutableStateFlow("Kotlin")
    val targetLanguage: StateFlow<String> = _targetLanguage.asStateFlow()

    private val _sourceCode = MutableStateFlow("")
    val sourceCode: StateFlow<String> = _sourceCode.asStateFlow()

    private val _translatedCode = MutableStateFlow("")
    val translatedCode: StateFlow<String> = _translatedCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _addComments = MutableStateFlow(true)
    val addComments: StateFlow<Boolean> = _addComments.asStateFlow()

    private val _addExplanation = MutableStateFlow(true)
    val addExplanation: StateFlow<Boolean> = _addExplanation.asStateFlow()

    val translationHistory: StateFlow<List<TranslationEntity>> = repository.allTranslations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSourceLanguage(language: String) {
        _sourceLanguage.value = language
    }

    fun setTargetLanguage(language: String) {
        _targetLanguage.value = language
    }

    fun setSourceCode(code: String) {
        _sourceCode.value = code
    }

    fun setAddComments(value: Boolean) {
        _addComments.value = value
    }

    fun setAddExplanation(value: Boolean) {
        _addExplanation.value = value
    }

    fun swapLanguages() {
        val src = _sourceLanguage.value
        val tgt = _targetLanguage.value
        if (src != "Auto-detect") {
            _sourceLanguage.value = tgt
            _targetLanguage.value = src
        }
    }

    fun translate() {
        val code = _sourceCode.value.trim()
        if (code.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val translation = repository.translateCode(
                    sourceLanguage = _sourceLanguage.value,
                    targetLanguage = _targetLanguage.value,
                    sourceCode = code,
                    addComments = _addComments.value,
                    addExplanation = _addExplanation.value
                )
                _translatedCode.value = translation
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown translation error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearInput() {
        _sourceCode.value = ""
        _translatedCode.value = ""
        _errorMessage.value = null
    }

    fun loadFromHistory(item: TranslationEntity) {
        _sourceLanguage.value = item.sourceLanguage
        _targetLanguage.value = item.targetLanguage
        _sourceCode.value = item.sourceCode
        _translatedCode.value = item.translatedCode
        _errorMessage.value = null
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteTranslation(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TranslationViewModel::class.java)) {
                val database = AppDatabase.getDatabase(application)
                val repository = TranslationRepository(
                    translationDao = database.translationDao(),
                    apiService = RetrofitClient.geminiApiService
                )
                @Suppress("UNCHECKED_CAST")
                return TranslationViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
