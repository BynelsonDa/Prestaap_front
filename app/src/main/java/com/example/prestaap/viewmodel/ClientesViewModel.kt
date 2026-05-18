package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prestaap.UiState
import com.example.prestaap.data.model.Cliente

class ClientesViewModel : ViewModel() {

    private val _clientes = MutableLiveData<UiState<List<Cliente>>>()
    val clientes: LiveData<UiState<List<Cliente>>> = _clientes

    private val allClientes = listOf(
        Cliente(1, "Eric Foreman",  "Barrio el Bosque",  "Pendiente", 4, 200000, 150000),
        Cliente(2, "James Wilson",  "Barrio el Bosque",  "Pendiente", 2, 360000, 360000),
        Cliente(3, "Gregory House", "La cordialidad",    "Pagado",    5, 500000, 0),
        Cliente(4, "Lisa Cuddy",    "Loma de Bolívar",   "Pagado",    1, 100000, 0)
    )

    private var queryNombre = ""
    private var queryEstado = "Todos"

    init { filtrar() }

    fun filtrarPorNombre(query: String) { queryNombre = query; filtrar() }
    fun filtrarPorEstado(estado: String) { queryEstado = estado; filtrar() }

    private fun filtrar() {
        _clientes.value = UiState.Loading
        val result = allClientes.filter { c ->
            val okNombre = c.nombre.contains(queryNombre, ignoreCase = true)
            val okEstado = queryEstado == "Todos" || c.estado == queryEstado
            okNombre && okEstado
        }
        _clientes.value = UiState.Success(result)
    }
}
