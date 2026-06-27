package com.example.mymvi.intent

sealed class CategoryIntent {
    data class UpdateField(val id: Int, val title: String, val cashBack: String) : CategoryIntent()
    object SaveAll: CategoryIntent()
}