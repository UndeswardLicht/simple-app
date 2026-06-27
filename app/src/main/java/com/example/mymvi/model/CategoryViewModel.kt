package com.example.mymvi.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymvi.data.Category
import com.example.mymvi.data.ClientRepository
import com.example.mymvi.intent.CategoryIntent
import com.example.mymvi.state.CategoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            ClientRepository.categories.collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
    }

    fun processIntent(intent: CategoryIntent) {
        when (intent) {
            is CategoryIntent.UpdateField -> {
                val updated = Category(intent.id, intent.title, intent.cashBack)
                _uiState.update { state ->
                    state.copy(pendingEdits = state.pendingEdits + (intent.id to updated))
                }
            }
            is CategoryIntent.SaveAll -> {
                val merged = _uiState.value.categories.map { category ->
                    _uiState.value.pendingEdits[category.id] ?: category
                }
                ClientRepository.updateCategories(merged)
                _uiState.update { it.copy(pendingEdits = emptyMap(), isSaved = true) }
            }
        }
    }
}