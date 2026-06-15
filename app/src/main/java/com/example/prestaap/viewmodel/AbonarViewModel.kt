package com.example.prestaap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaap.UiState
import com.example.prestaap.data.model.AbonoCreditoRequest
import com.example.prestaap.data.model.AbonoResponse
import com.example.prestaap.data.model.CreditoResumen
import com.example.prestaap.data.model.PagoResponse
import com.example.prestaap.data.repository.PagosRepository
import kotlinx.coroutines.launch

class AbonarViewModel : ViewModel() {

    private val repository = PagosRepository()

    private val _pago = MutableLiveData<UiState<PagoResponse>>()
    val pago: LiveData<UiState<PagoResponse>> = _pago

    fun registrarPago(creditoId: Int, request: AbonoCreditoRequest) {
        _pago.value = UiState.Loading
        viewModelScope.launch {
            _pago.value = repository.crearPago(creditoId, request)
        }
    }

    /**
     * "Abonar a todos" (solo frontend): reparte el monto entre los créditos del cliente
     * (de menor id primero), topado al saldo de cada uno, haciendo un POST /pagos/credito/{id}
     * por crédito. Arma un recibo agregado con todas las cuotas a las que se abonó.
     */
    fun registrarPagoTodos(
        creditos: List<CreditoResumen>,
        monto: Double,
        fechaPago: String,
        metodoPagoId: Int
    ) {
        _pago.value = UiState.Loading
        viewModelScope.launch {
            var restante = monto
            var aplicado = 0.0
            val abonosAcumulados = mutableListOf<AbonoResponse>()
            var primerPago: PagoResponse? = null

            for (credito in creditos.sortedBy { it.id }) {
                if (restante <= 0) break
                val aPagar = minOf(restante, credito.montoRestante)
                if (aPagar <= 0) continue

                val resultado = repository.crearPago(
                    credito.id,
                    AbonoCreditoRequest(aPagar, fechaPago, metodoPagoId, null)
                )
                when (resultado) {
                    is UiState.Success -> {
                        if (primerPago == null) primerPago = resultado.data
                        abonosAcumulados.addAll(resultado.data.abonos)
                        aplicado += aPagar
                        restante -= aPagar
                    }
                    is UiState.Error -> { _pago.value = resultado; return@launch }
                    else -> {}
                }
            }

            val base = primerPago
            if (base == null) {
                _pago.value = UiState.Error("Ningún crédito tiene saldo pendiente para abonar")
                return@launch
            }
            _pago.value = UiState.Success(base.copy(montoTotal = aplicado, abonos = abonosAcumulados))
        }
    }
}
