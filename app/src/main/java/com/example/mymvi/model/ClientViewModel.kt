package com.example.mymvi.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymvi.data.Client
import com.example.mymvi.data.ClientRepository
import com.example.mymvi.intent.ClientIntent
import com.example.mymvi.state.ClientUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientViewModel : ViewModel() {
   private val _uiState = MutableStateFlow(ClientUiState())
   val uiState: StateFlow<ClientUiState> = _uiState.asStateFlow()
   val clients: StateFlow<List<Client>> = ClientRepository.clients

   init {
       viewModelScope.launch {
          ClientRepository.clients.collect { list ->
             _uiState.update { it.copy(clients = list) }
          }
       }
   }

   fun processIntent(intent: ClientIntent) {
      when (intent) {
         is ClientIntent.AddClient -> addClient()
         is ClientIntent.DeleteClient -> deleteClient(intent.id)
         is ClientIntent.NavigateToEdit -> {
            _uiState.update { it.copy(navigateToEdit = intent.id) }
         }
      }
   }

   fun onNavigatedToEdit() {
      _uiState.update { it.copy(navigateToEdit = null) }
   }

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