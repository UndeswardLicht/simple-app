package com.example.mymvi.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mymvi.data.ClientRepository
import com.example.mymvi.intent.EditClientIntent
import com.example.mymvi.state.EditClientUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditClientViewModel(private val clientId: Int) : ViewModel() {
    private val _uiState = MutableStateFlow(EditClientUiState())
    val uiState: StateFlow<EditClientUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                ClientRepository.getClientById(clientId),
                ClientRepository.categories
            ) { client, categories ->
                _uiState.update { it.copy(client = client, categories = categories) }
            }.collect {}
        }
    }

    fun processIntent(intent: EditClientIntent) {
        when (intent) {
            is EditClientIntent.Save -> {
                viewModelScope.launch {
                    _uiState.value.client?.let { existingClient ->
                        ClientRepository.updateClient(
                            existingClient.copy(
                                name = intent.name,
                                categoryId = intent.categoryId
                            )
                        )
                    }
                    _uiState.update { it.copy(isDone = true) }
                }
            }

            is EditClientIntent.Cancel -> {
                _uiState.update { it.copy(isDone = true) }
            }
        }
    }

    class Factory(private val clientId: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditClientViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditClientViewModel(clientId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
