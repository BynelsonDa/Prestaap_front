package com.example.prestaap.data.repository

import com.example.prestaap.UiState
import com.example.prestaap.data.api.RetrofitClient
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
}
