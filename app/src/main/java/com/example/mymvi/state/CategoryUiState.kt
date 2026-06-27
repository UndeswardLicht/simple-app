package com.example.mymvi.state

import com.example.mymvi.data.Category

data class CategoryUiState (
    val categories: List<Category> = emptyList(),
    val pendingEdits: Map<Int, Category> = emptyMap(),
    val isSaved: Boolean = false
) {
    // Produces the final merged list the adapter should display
    fun mergedCategories(): List<Category> =
        categories.map { pendingEdits[it.id] ?: it }
}