package com.example.mymvi.state

import com.example.mymvi.data.Client

data class ClientUiState (
    val clients: List<Client> = emptyList(),
    val isLoading: Boolean = false,
    val navigateToEdit: Int? = null
)