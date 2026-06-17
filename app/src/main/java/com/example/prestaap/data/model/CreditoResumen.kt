package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

data class CreditoResumen(
    @SerializedName("id") val id: Int,
    @SerializedName("label") val label: String,
    @SerializedName("montoPrestamo") val montoPrestamo: Double,
    @SerializedName("montoRestante") val montoRestante: Double,
    @SerializedName("cuotasPagadas") val cuotasPagadas: Int,
    @SerializedName("totalCuotas") val totalCuotas: Int
) {
    val estado: String get() = if (montoRestante == 0.0) "Pagado" else "Pendiente"
}
