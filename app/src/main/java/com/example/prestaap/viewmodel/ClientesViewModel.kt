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
        Cliente(1, "Eric Foreman",  "Barrio el Bosque", "Pendiente", 4, 200000, 150000,
                "1005052841", "3062704100", "Calle 10 y 11 Sector Quinta Veléz", 5000000, 10),
        Cliente(2, "James Wilson",  "Barrio el Bosque", "Pendiente", 2, 360000, 360000,
                "1005052842", "3062704101", "Calle 5 # 12-34 Barrio el Bosque",  3600000, 2),
        Cliente(3, "Gregory House", "La cordialidad",   "Pagado",    5, 500000, 0,
                "1005052843", "3062704102", "Carrera 8 # 15-20 La Cordialidad",  5000000, 5),
        Cliente(4, "Lisa Cuddy",    "Loma de Bolívar",  "Pagado",    1, 100000, 0,
                "1005052844", "3062704103", "Calle 3 # 8-10 Loma de Bolívar",   1000000, 1)
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
