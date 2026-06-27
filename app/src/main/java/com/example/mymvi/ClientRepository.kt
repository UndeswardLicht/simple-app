package com.example.mymvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ClientRepository {
    private val _clients = MutableStateFlow<List<Client>>(
        listOf(
            Client(1, "John Doe", "john@example.com", 1),
            Client(2, "Jane Smith", "jane@example.com", 2)
        )
    )

    private val _categories = MutableStateFlow<List<Category>>(
        listOf(
            Category(1, "REGULAR", "1%"),
            Category(2, "PREMIUM", "5%"),
            Category(3, "VIP", "10%")
        )
    )

    val clients: StateFlow<List<Client>> = _clients.asStateFlow();
    val categories: StateFlow<List<Category>> = _categories.asStateFlow();

    fun getClientById(id: Int): Client? {
        return _clients.value.find { it.id == id }
    }

    fun addClient(client: Client) {
        return _clients.update { it + client }
    }

    fun updateClient(updatedClient: Client){
        _clients.update { currentList ->
            currentList.map { if (it.id ==updatedClient.id) updatedClient else it }
        }
    }

    fun deleteClient(clientId: Int) {
        _clients.update { currentList ->
            currentList.filter { it.id != clientId }
        }
    }

    fun updateCategories(updatedCategories: List<Category>) {
        _categories.value = updatedCategories
    }
}