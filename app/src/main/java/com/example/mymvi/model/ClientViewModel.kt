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
         is ClientIntent.NavigatedToEdit -> {
            _uiState.update { it.copy(navigateToEdit = null) }
         }
      }
   }

   private fun addClient() {
      viewModelScope.launch {
         val currentList = ClientRepository.clients.value
         val nextId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
         ClientRepository.addClient(
            Client(
               id = nextId,
               name = "New Client $nextId",
               email = "client$nextId@example.com"
            )
         )
      }
   }

   private fun deleteClient(clientId: Int) {
      viewModelScope.launch {
         ClientRepository.deleteClient(clientId)
      }
   }
}