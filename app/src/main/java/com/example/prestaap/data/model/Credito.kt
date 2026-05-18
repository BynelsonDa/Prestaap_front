package com.example.prestaap.data.model

data class Credito(
    val id: Int,
    val nombre: String,
    val estado: String,
    val prestado: Long,
    val restante: Long,
    val cuotasPagadas: Int,
    val totalCuotas: Int,
    val fechaCredito: String = "",
    val frecuenciaPago: String = "",
    val porcentajeInteres: Double = 0.0,
    val interesesPorCuota: Long = 0L,
    val valorCuotaCapital: Long = 0L,
    val cuotasVencidas: Int = 0,
    val totalAbonado: Long = 0L,
    val deudaTotal: Long = 0L,
    val fechaVencimiento: String = ""
)
