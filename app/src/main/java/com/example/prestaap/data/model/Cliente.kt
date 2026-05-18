package com.example.prestaap.data.model

data class Cliente(
    val id: Int,
    val nombre: String,
    val zona: String,
    val estado: String,
    val creditos: Int,
    val saldoPrestado: Long,
    val saldoPendiente: Long
)
