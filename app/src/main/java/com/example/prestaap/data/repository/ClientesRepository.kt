package com.example.prestaap.data.repository

import com.example.prestaap.UiState
import com.example.prestaap.data.api.RetrofitClient
import com.example.prestaap.data.model.ClienteZona
import com.example.prestaap.data.model.CreditoDetalle
import com.example.prestaap.data.model.CreditoResumen
import com.example.prestaap.data.model.NuevoClienteRequest

class ClientesRepository {
    suspend fun crearCliente(request: NuevoClienteRequest): UiState<Unit> {
        return try {
            val response = RetrofitClient.apiService.crearCliente(request)
            if (response.isSuccessful) {
                UiState.Success(Unit)
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al guardar cliente")
        }
    }

    suspend fun getClientesPorZona(zonaId: Int): UiState<List<ClienteZona>> {
        return try {
            val response = RetrofitClient.apiService.getClientesPorZona(zonaId)
            if (response.isSuccessful) {
                UiState.Success(response.body() ?: emptyList())
            } else {
                val errorBody = response.errorBody()?.string() ?: "(sin cuerpo)"
                UiState.Error("Error ${response.code()} para zonaId=$zonaId\n$errorBody")
            }
        } catch (e: Exception) {
            UiState.Error("Excepción zonaId=$zonaId: ${e.message}")
        }
    }

    suspend fun getDetalleCredito(id: Int): UiState<CreditoDetalle> {
        return try {
            val response = RetrofitClient.apiService.getDetalleCredito(id)
            if (response.isSuccessful) {
                UiState.Success(response.body()!!)
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al cargar detalle")
        }
    }

    suspend fun getResumenCreditos(cedula: Long): UiState<List<CreditoResumen>> {
        return try {
            val response = RetrofitClient.apiService.getResumenCreditos(cedula)
            if (response.isSuccessful) {
                UiState.Success(response.body() ?: emptyList())
            } else {
                UiState.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al cargar créditos")
        }
    }
}
