package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

data class ClienteResponse(
    @SerializedName("cedula") val cedula: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("zona_id") val zonaId: Int?
)
