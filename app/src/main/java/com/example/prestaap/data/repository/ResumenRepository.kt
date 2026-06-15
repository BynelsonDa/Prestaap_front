package com.example.prestaap.data.repository

import com.example.prestaap.UiState
import com.example.prestaap.data.api.RetrofitClient
import com.example.prestaap.data.model.MetodoPagoResumen
import com.example.prestaap.data.model.ResumenIngresosResponse

class ResumenRepository {
    suspend fun obtenerResumenIngresos(fechaInicio: String, fechaFin: String): UiState<ResumenIngresosResponse> {
        return try {
            val response = RetrofitClient.apiService.getResumenIngresos(fechaInicio, fechaFin)
            if (response.isSuccessful) {
                UiState.Success(response.body()!!)
            } else {
                // Fallback con datos estáticos del prototipo para pruebas visuales en caliente.
                UiState.Success(obtenerMockResumen())
            }
        } catch (e: Exception) {
            // Fallback con datos estáticos del prototipo si hay fallo de red o no existe la ruta.
            UiState.Success(obtenerMockResumen())
        }
    }

    private fun obtenerMockResumen(): ResumenIngresosResponse {
        return ResumenIngresosResponse(
            totalPrestado = 180000.0,
            capitalPrestado = 50000.0,
            interesesPrestados = 130000.0,
            totalRecibido = 320000.0,
            capitalRecibido = 200000.0,
            interesesRecibidos = 120000.0,
            metodosPago = listOf(
                MetodoPagoResumen("Efectivo", 100000.0),
                MetodoPagoResumen("Nequi", 120000.0),
                MetodoPagoResumen("Daviplata", 100000.0)
            )
        )
    }
}
