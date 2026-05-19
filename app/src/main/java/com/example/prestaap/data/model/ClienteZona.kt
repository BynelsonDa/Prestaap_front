package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

data class ClienteZona(
    @SerializedName("cedula") val cedula: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("cantidadCreditos") val cantidadCreditos: Int,
    @SerializedName("montoTotalPrestamo") val montoTotalPrestamo: Double,
    @SerializedName("saldoPendiente") val saldoPendiente: Double
) {
    val estado: String get() = if (saldoPendiente == 0.0) "Pagado" else "Pendiente"
}
