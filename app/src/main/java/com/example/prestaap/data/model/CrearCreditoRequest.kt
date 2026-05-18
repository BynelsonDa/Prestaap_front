package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

data class CrearCreditoRequest(
    @SerializedName("montoPrestamo") val montoPrestamo: Double,
    @SerializedName("interes") val interes: Double,
    @SerializedName("numeroDeCuotas") val numeroDeCuotas: Int,
    @SerializedName("fechaPrestamo") val fechaPrestamo: String,
    @SerializedName("fechaLimite") val fechaLimite: String,
    @SerializedName("frecuenciaPagoId") val frecuenciaPagoId: Int,
    @SerializedName("clienteCedula") val clienteCedula: Long
)
