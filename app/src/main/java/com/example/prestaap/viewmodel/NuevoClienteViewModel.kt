package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.model.NuevoClienteRequest
import com.example.prestaap.data.model.Zona
import com.example.prestaap.data.repository.ClientesRepository
import com.example.prestaap.data.repository.ZonasRepository
import kotlinx.coroutines.launch

class NuevoClienteViewModel : ViewModel() {

    private val zonasRepository = ZonasRepository()
    private val clientesRepository = ClientesRepository()

    private val _zonas = MutableLiveData<List<Zona>>()
    val zonas: LiveData<List<Zona>> = _zonas

    private val _guardadoState = MutableLiveData<UiState<Unit>>()
    val guardadoState: LiveData<UiState<Unit>> = _guardadoState

    init { cargarZonas() }

    private fun cargarZonas() {
        viewModelScope.launch {
            val result = zonasRepository.getZonas()
            if (result is UiState.Success) _zonas.value = result.data
        }
    }

    fun guardarCliente(request: NuevoClienteRequest) {
        _guardadoState.value = UiState.Loading
        viewModelScope.launch {
            _guardadoState.value = clientesRepository.crearCliente(request)
        }
    }
}
