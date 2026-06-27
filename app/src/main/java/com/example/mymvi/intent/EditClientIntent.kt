package com.example.mymvi.intent

sealed class EditClientIntent {
    data class Save(val name: String, val categoryId: Int?): EditClientIntent()
    object Cancel: EditClientIntent()
}