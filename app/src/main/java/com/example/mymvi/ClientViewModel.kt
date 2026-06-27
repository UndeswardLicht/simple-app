package com.example.mymvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientViewModel : ViewModel() {
   val clients: StateFlow<List<Client>> = ClientRepository.clients

   fun addClient() {
      viewModelScope.launch {
         val currentList = clients.value
         val nextId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
         val newClient = Client(
            id = nextId,
            name = "New Client $nextId",
            email = "client$nextId@example.com"
         )
         ClientRepository.addClient(newClient)
      }
   }

   fun deleteClient(clientId: Int) {
      viewModelScope.launch {
         ClientRepository.deleteClient(clientId)
      }
   }
}