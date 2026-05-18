package com.example.prestaap.data.model

data class NuevoClienteRequest(
    val cedula: Long,
    val nombre: String,
    val direccion: String,
    val zonaId: Int,
    val referencia: ReferenciaRequest? = null
)

data class ReferenciaRequest(
    val cedula: Long,
    val nombre: String,
    val celular: Long,
    val direccion: String? = null
)
