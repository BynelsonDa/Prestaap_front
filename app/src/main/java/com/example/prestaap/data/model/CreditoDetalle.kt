package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

data class CreditoDetalle(
    @SerializedName("totalPrestado") val totalPrestado: Double,
    @SerializedName("fechaPrestamo") val fechaPrestamo: String,
    @SerializedName("fechaLimite") val fechaLimite: String,
    @SerializedName("interes") val interes: Double,
    @SerializedName("numeroDeCuotas") val numeroDeCuotas: Int,
    @SerializedName("frecuenciaPago") val frecuenciaPago: String,
   // @SerializedName("valorInteresPorCuota") val valorInteresPorCuota: Double,
    @SerializedName("cuotasPagadas") val cuotasPagadas: Int,
    @SerializedName("cuotasVencidas") val cuotasVencidas: Int,
    @SerializedName("totalAbonado") val totalAbonado: Double,
    @SerializedName("deudaTotal") val deudaTotal: Double
) {
    val estado: String get() = if (deudaTotal == 0.0) "Pagado" else "Pendiente"
}
