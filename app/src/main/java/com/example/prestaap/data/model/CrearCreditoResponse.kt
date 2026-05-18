package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

data class CrearCreditoResponse(
    @SerializedName("id_credito") val idCredito: Int?,
    @SerializedName("mensaje") val mensaje: String?
)
