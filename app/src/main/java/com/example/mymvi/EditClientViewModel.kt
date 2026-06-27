package com.example.mymvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditClientViewModel(private val clientId: Int) : ViewModel() {
    private val _client = MutableStateFlow(ClientRepository.getClientById(clientId))
    val client: StateFlow<Client?> = _client.asStateFlow()

    val categories: StateFlow<List<Category>> = ClientRepository.categories

    fun saveClient(newName: String, categoryId: Int?) {
        val currentClient = _client.value
        if (currentClient != null) {
            val updatedClient = currentClient.copy(name = newName, categoryId = categoryId)
            ClientRepository.updateClient(updatedClient)
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
