package com.example.mymvi

data class Client(
    val id: Int,
    val name: String,
    val email: String,
    val categoryId: Int? = null
)
