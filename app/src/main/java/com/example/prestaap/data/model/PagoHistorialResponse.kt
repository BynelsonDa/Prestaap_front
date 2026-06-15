package com.example.prestaap.data.model

data class PagoHistorialResponse(
    val idPago: Int,
    val fechaPago: String,
    val montoTotal: Double,
    val metodoPago: String,
    val numeroCredito: Int,
    val numeroCuota: Int,
    val capitalAbonado: Double,
    val interesAbonado: Double
)
