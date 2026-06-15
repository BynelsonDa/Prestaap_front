package com.example.prestaap.data.model

data class ResumenIngresosResponse(
    val totalPrestado: Double,
    val capitalPrestado: Double,
    val interesesPrestados: Double,
    val totalRecibido: Double,
    val capitalRecibido: Double,
    val interesesRecibidos: Double,
    val metodosPago: List<MetodoPagoResumen>
)
