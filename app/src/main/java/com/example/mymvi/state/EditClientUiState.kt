package com.example.mymvi.state

import com.example.mymvi.data.Category
import com.example.mymvi.data.Client

data class EditClientUiState(
    val client: Client? = null,
    val categories: List<Category> = emptyList(),
    val isDone: Boolean = false
)