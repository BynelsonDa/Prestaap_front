package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.model.ResumenIngresosResponse
import com.example.prestaap.data.repository.ResumenRepository
import kotlinx.coroutines.launch

class ResumenViewModel : ViewModel() {
    private val repository = ResumenRepository()

    private val _resumenState = MutableLiveData<UiState<ResumenIngresosResponse>>()
    val resumenState: LiveData<UiState<ResumenIngresosResponse>> = _resumenState

    fun cargarResumen(fechaInicio: String, fechaFin: String) {
        _resumenState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.obtenerResumenIngresos(fechaInicio, fechaFin)
            _resumenState.value = result
        }
    }
}
