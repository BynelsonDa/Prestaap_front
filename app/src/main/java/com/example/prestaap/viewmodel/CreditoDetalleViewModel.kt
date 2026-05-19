package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.model.CreditoDetalle
import com.example.prestaap.data.repository.ClientesRepository
import kotlinx.coroutines.launch

class CreditoDetalleViewModel : ViewModel() {

    private val repository = ClientesRepository()

    private val _detalle = MutableLiveData<UiState<CreditoDetalle>>()
    val detalle: LiveData<UiState<CreditoDetalle>> = _detalle

    fun fetchDetalle(creditoId: Int) {
        _detalle.value = UiState.Loading
        viewModelScope.launch {
            _detalle.value = repository.getDetalleCredito(creditoId)
        }
    }
}
