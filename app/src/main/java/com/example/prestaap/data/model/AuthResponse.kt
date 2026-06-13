package com.example.prestaap.data.model

data class AuthResponse(
    val message: String,
    val uid: String,
    val email: String,
    val nombre: String,
    val rol: String
)
