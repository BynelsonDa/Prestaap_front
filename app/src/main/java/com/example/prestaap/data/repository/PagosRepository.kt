package com.example.prestaap.data.repository

import com.example.prestaap.UiState
import com.example.prestaap.data.api.RetrofitClient
import com.example.prestaap.data.model.AbonoCreditoRequest
import com.example.prestaap.data.model.PagoResponse

class PagosRepository {

    suspend fun crearPago(creditoId: Int, request: AbonoCreditoRequest): UiState<PagoResponse> {
        return try {
            val response = RetrofitClient.apiService.crearPagoCredito(creditoId, request)
            if (response.isSuccessful && response.body() != null) {
                UiState.Success(response.body()!!)
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al registrar el pago")
        }
    }
}
