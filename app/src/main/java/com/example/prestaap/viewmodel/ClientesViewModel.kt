package com.example.prestaap.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.model.ClienteZona
import com.example.prestaap.data.repository.ClientesRepository
import kotlinx.coroutines.launch

class ClientesViewModel : ViewModel() {

    private val repository = ClientesRepository()

    private val _clientes = MutableLiveData<UiState<List<ClienteZona>>>()
    val clientes: LiveData<UiState<List<ClienteZona>>> = _clientes

    private var allClientes: List<ClienteZona> = emptyList()
    private var queryNombre = ""
    private var queryEstado = "Todos"

    fun fetchClientes(zonaId: Int) {
        Log.d("ClientesVM", "fetchClientes zonaId=$zonaId → URL: clientes/zona/$zonaId")
        _clientes.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getClientesPorZona(zonaId)
            if (result is UiState.Success) {
                allClientes = result.data
                filtrar()
            } else {
                _clientes.value = result
            }
        }
    }

    fun filtrarPorNombre(query: String) { queryNombre = query; filtrar() }
    fun filtrarPorEstado(estado: String) { queryEstado = estado; filtrar() }

    private fun filtrar() {
        val result = allClientes.filter { c ->
            val okNombre = c.nombre.contains(queryNombre, ignoreCase = true)
            val okEstado = queryEstado == "Todos" || c.estado == queryEstado
            okNombre && okEstado
        }
        _clientes.value = UiState.Success(result)
    }
}
