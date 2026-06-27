package com.example.mymvi.data

data class Client(
    val id: Int,
    val name: String,
    val email: String,
    val categoryId: Int? = null
)