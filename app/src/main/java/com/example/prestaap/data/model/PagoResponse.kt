package com.example.prestaap.data.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de POST /pagos/credito/{id}. Solo mapea los campos que el recibo necesita.
 * La distribución del pago vive en [abonos]: un abono por cada cuota a la que se le abonó.
 */
data class PagoResponse(
    @SerializedName("id_pago") val idPago: Int,
    @SerializedName("monto_total") val montoTotal: Double,
    @SerializedName("fecha_pago") val fechaPago: String?,   // "2026-06-15T14:30:00"
    @SerializedName("cliente") val cliente: ClientePagoResponse?,
    @SerializedName("metodoDePago") val metodoDePago: MetodoPagoResponse?,
    @SerializedName("abonos") val abonos: List<AbonoResponse> = emptyList()
)

data class ClientePagoResponse(
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("cedula") val cedula: Long?
)

data class MetodoPagoResponse(
    @SerializedName("nombre") val nombre: String?
)

data class AbonoResponse(
    @SerializedName("total_abonado") val totalAbonado: Double,
    @SerializedName("capital_abonado") val capitalAbonado: Double,
    @SerializedName("interes_abonado") val interesAbonado: Double,
    @SerializedName("cuota") val cuota: CuotaResponse?
)

data class CuotaResponse(
    @SerializedName("numero_de_cuota") val numeroDeCuota: Int,
    @SerializedName("estado") val estado: EstadoResponse? = null
)

data class EstadoResponse(
    @SerializedName("nombre") val nombre: String?
)
