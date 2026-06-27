package com.example.mymvi.intent

sealed class ClientIntent {
    object AddClient: ClientIntent()
    data class DeleteClient(val id: Int): ClientIntent()
    data class NavigateToEdit(val id: Int): ClientIntent()

}