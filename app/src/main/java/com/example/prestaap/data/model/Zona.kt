package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

data class Zona(
    @SerializedName("id_zona") val id: Int,
    val nombre: String
)
