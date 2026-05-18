package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.api.RetrofitClient
import com.example.prestaap.data.model.ClienteResponse
import kotlinx.coroutines.launch

class SeleccionarClienteViewModel : ViewModel() {

    private val _clientes = MutableLiveData<UiState<List<ClienteResponse>>>()
    val clientes: LiveData<UiState<List<ClienteResponse>>> = _clientes

    private var todosLosClientes: List<ClienteResponse> = emptyList()

    init {
        fetchClientes()
    }

    fun fetchClientes() {
        _clientes.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getClientes()
                if (response.isSuccessful) {
                    todosLosClientes = response.body() ?: emptyList()
                    _clientes.value = UiState.Success(todosLosClientes)
                } else {
                    _clientes.value = UiState.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                _clientes.value = UiState.Error(e.message ?: "Error al cargar clientes")
            }
        }
    }

    fun filtrar(query: String) {
        val lista = if (query.isBlank()) {
            todosLosClientes
        } else {
            todosLosClientes.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                        it.cedula.toString().contains(query)
            }
        }
        _clientes.value = UiState.Success(lista)
    }
}
