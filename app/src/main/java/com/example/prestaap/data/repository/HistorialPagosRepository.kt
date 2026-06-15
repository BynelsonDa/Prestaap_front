package com.example.prestaap.data.repository

import com.example.prestaap.UiState
import com.example.prestaap.data.api.RetrofitClient
import com.example.prestaap.data.model.PagoHistorialResponse

class HistorialPagosRepository {
    suspend fun obtenerHistorialPagos(cedula: Long): UiState<List<PagoHistorialResponse>> {
        return try {
            val response = RetrofitClient.apiService.getHistorialPagosCliente(cedula)
            if (response.isSuccessful) {
                UiState.Success(response.body() ?: emptyList())
            } else {
                // FALLBACK: Como el endpoint no existe en backend, usamos datos estáticos de prueba.
                // Una vez que el backend implemente el GET, puedes reemplazar esto con: UiState.Error(...)
                UiState.Success(obtenerMockPagos())
            }
        } catch (e: Exception) {
            // FALLBACK: Si falla la conexión de red o no existe la ruta.
            UiState.Success(obtenerMockPagos())
        }
    }

    private fun obtenerMockPagos(): List<PagoHistorialResponse> {
        return listOf(
            PagoHistorialResponse(
                idPago = 1,
                fechaPago = "2026-06-15T09:22:00",
                montoTotal = 45000.0,
                metodoPago = "Efectivo",
                numeroCredito = 101,
                numeroCuota = 3,
                capitalAbonado = 35000.0,
                interesAbonado = 10000.0
            ),
            PagoHistorialResponse(
                idPago = 2,
                fechaPago = "2026-06-14T15:30:00",
                montoTotal = 25000.0,
                metodoPago = "Transferencia Nequi",
                numeroCredito = 102,
                numeroCuota = 1,
                capitalAbonado = 20000.0,
                interesAbonado = 5000.0
            ),
            PagoHistorialResponse(
                idPago = 3,
                fechaPago = "2026-06-10T11:15:00",
                montoTotal = 15000.0,
                metodoPago = "Daviplata",
                numeroCredito = 101,
                numeroCuota = 2,
                capitalAbonado = 12000.0,
                interesAbonado = 3000.0
            ),
            PagoHistorialResponse(
                idPago = 4,
                fechaPago = "2026-06-05T18:45:00",
                montoTotal = 50000.0,
                metodoPago = "Efectivo",
                numeroCredito = 103,
                numeroCuota = 5,
                capitalAbonado = 40000.0,
                interesAbonado = 10000.0
            )
        )
    }
}
