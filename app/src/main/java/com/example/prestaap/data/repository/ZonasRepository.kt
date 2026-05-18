package com.example.prestaap.data.repository

import com.example.prestaap.UiState
import com.example.prestaap.data.api.RetrofitClient
import com.example.prestaap.data.model.Zona

class ZonasRepository {
    suspend fun getZonas(): UiState<List<Zona>> {
        return try {
            UiState.Success(RetrofitClient.apiService.getZonas())
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al cargar zonas")
        }
    }
}
