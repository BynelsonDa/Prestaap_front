package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

/**
 * Body para POST /pagos/credito/{id}. Mapea AbonoCreditoDTO del backend.
 * bancoId es null cuando el pago es en efectivo.
 */
data class AbonoCreditoRequest(
    @SerializedName("monto") val monto: Double,
    @SerializedName("fechaPago") val fechaPago: String,   // ISO LocalDateTime: "2026-06-15T14:30:00"
    @SerializedName("metodoPagoId") val metodoPagoId: Int,
    @SerializedName("bancoId") val bancoId: Int? = null
)
