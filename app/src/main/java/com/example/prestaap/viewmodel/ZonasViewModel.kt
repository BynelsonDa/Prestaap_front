package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.model.Zona
import com.example.prestaap.data.repository.ZonasRepository
import kotlinx.coroutines.launch

class ZonasViewModel : ViewModel() {

    private val repository = ZonasRepository()

    private val _zonas = MutableLiveData<UiState<List<Zona>>>()
    val zonas: LiveData<UiState<List<Zona>>> = _zonas

    private var allZonas: List<Zona> = emptyList()

    init { fetchZonas() }

    fun fetchZonas() {
        _zonas.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getZonas()
            if (result is UiState.Success) allZonas = result.data
            _zonas.value = result
        }
    }

    fun filtrar(query: String) {
        val filtered = if (query.isBlank()) allZonas
                       else allZonas.filter { it.nombre.contains(query, ignoreCase = true) }
        _zonas.value = UiState.Success(filtered)
    }
}
