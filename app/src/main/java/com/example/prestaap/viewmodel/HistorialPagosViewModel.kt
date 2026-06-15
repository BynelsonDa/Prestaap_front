package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.model.PagoHistorialResponse
import com.example.prestaap.data.repository.HistorialPagosRepository
import kotlinx.coroutines.launch

class HistorialPagosViewModel : ViewModel() {
    private val repository = HistorialPagosRepository()

    private val _pagosState = MutableLiveData<UiState<List<PagoHistorialResponse>>>()
    val pagosState: LiveData<UiState<List<PagoHistorialResponse>>> = _pagosState

    fun cargarHistorial(cedula: Long) {
        _pagosState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.obtenerHistorialPagos(cedula)
            _pagosState.value = result
        }
    }
}
