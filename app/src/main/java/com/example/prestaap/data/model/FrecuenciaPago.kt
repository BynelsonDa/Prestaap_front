package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

data class FrecuenciaPago(
    @SerializedName("id_referencia_pago") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("dias") val dias: Int
)
