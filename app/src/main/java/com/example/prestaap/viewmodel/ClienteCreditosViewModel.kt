package com.example.prestaap.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.model.CreditoResumen
import com.example.prestaap.data.repository.ClientesRepository
import kotlinx.coroutines.launch

class ClienteCreditosViewModel : ViewModel() {

    private val repository = ClientesRepository()

    private val _creditos = MutableLiveData<UiState<List<CreditoResumen>>>()
    val creditos: LiveData<UiState<List<CreditoResumen>>> = _creditos

    private var allCreditos: List<CreditoResumen> = emptyList()
    private var showingAll = true
    private var queryEstado = "Todos"

    fun fetchCreditos(cedula: Long) {
        Log.d("CreditosVM", "fetchCreditos cedula=$cedula → URL: creditos/cliente/$cedula/resume")
        _creditos.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getResumenCreditos(cedula)
            Log.d("CreditosVM", "resultado=$result")
            if (result is UiState.Success) {
                Log.d("CreditosVM", "créditos recibidos: ${result.data.size} → ${result.data}")
                allCreditos = result.data
                filtrar()
            } else {
                _creditos.value = result
            }
        }
    }

    fun mostrarTodos() { showingAll = true; filtrar() }
    fun mostrarACobrar() { showingAll = false; filtrar() }
    fun filtrarPorEstado(estado: String) { queryEstado = estado; filtrar() }

    private fun filtrar() {
        var list = if (showingAll) allCreditos
                   else allCreditos.filter { it.estado == "Pendiente" || it.estado == "Atrasado" }
        if (queryEstado != "Todos") list = list.filter { it.estado == queryEstado }
        _creditos.value = UiState.Success(list)
    }
}
