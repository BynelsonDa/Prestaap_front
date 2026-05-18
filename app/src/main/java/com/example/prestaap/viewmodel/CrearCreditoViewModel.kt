package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.api.RetrofitClient
import com.example.prestaap.data.model.CrearCreditoRequest
import com.example.prestaap.data.model.CrearCreditoResponse
import com.example.prestaap.data.model.FrecuenciaPago
import kotlinx.coroutines.launch

class CrearCreditoViewModel : ViewModel() {

    private val _frecuencias = MutableLiveData<UiState<List<FrecuenciaPago>>>()
    val frecuencias: LiveData<UiState<List<FrecuenciaPago>>> = _frecuencias

    private val _crearState = MutableLiveData<UiState<CrearCreditoResponse>>()
    val crearState: LiveData<UiState<CrearCreditoResponse>> = _crearState

    init {
        fetchFrecuencias()
    }

    fun fetchFrecuencias() {
        _frecuencias.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getFrecuenciasPago()
                if (response.isSuccessful) {
                    _frecuencias.value = UiState.Success(response.body() ?: emptyList())
                } else {
                    _frecuencias.value = UiState.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                _frecuencias.value = UiState.Error(e.message ?: "Error al cargar frecuencias")
            }
        }
    }

    fun crearCredito(request: CrearCreditoRequest) {
        _crearState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.crearCredito(request)
                if (response.isSuccessful) {
                    _crearState.value = UiState.Success(response.body() ?: CrearCreditoResponse(null, "Crédito creado"))
                } else {
                    _crearState.value = UiState.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                _crearState.value = UiState.Error(e.message ?: "Error al crear crédito")
            }
        }
    }
}
