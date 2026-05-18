package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prestaap.UiState
import com.example.prestaap.data.model.Zona

class ClientesViewModel : ViewModel() {

    private val _zonas = MutableLiveData<UiState<List<Zona>>>()
    val zonas: LiveData<UiState<List<Zona>>> = _zonas

    private val allZonas = listOf(
        Zona(1, "Los Patios"),
        Zona(2, "Villa de Rosario"),
        Zona(3, "Cúcuta"),
        Zona(4, "Zulia")
    )

    init { cargarZonas() }

    fun cargarZonas(query: String = "") {
        _zonas.value = UiState.Loading
        val result = if (query.isBlank()) allZonas
                     else allZonas.filter { it.nombre.contains(query, ignoreCase = true) }
        _zonas.value = UiState.Success(result)
    }
}
