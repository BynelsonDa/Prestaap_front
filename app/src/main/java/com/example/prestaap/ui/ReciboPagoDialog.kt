package com.example.prestaap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestaap.data.model.PagoResponse
import com.example.prestaap.databinding.DialogReciboPagoBinding
import com.google.gson.Gson

/** Recibo de pago: muestra los datos del pago + la distribución por cuotas que devolvió el backend. */
class ReciboPagoDialog : DialogFragment() {

    private var _binding: DialogReciboPagoBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(pagoJson: String, creditoLabel: String) = ReciboPagoDialog().apply {
            arguments = Bundle().apply {
                putString("pagoJson", pagoJson)
                putString("creditoLabel", creditoLabel)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogReciboPagoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pago = Gson().fromJson(arguments?.getString("pagoJson"), PagoResponse::class.java)
        val creditoLabel = arguments?.getString("creditoLabel") ?: "Crédito"

        binding.tvCliente.text = pago?.cliente?.nombre ?: "-"
        binding.tvCredito.text = Regex("\\d+").find(creditoLabel)?.value ?: creditoLabel
        binding.tvMetodo.text  = pago?.metodoDePago?.nombre ?: "-"
        binding.tvMonto.text   = formatPeso((pago?.montoTotal ?: 0.0).toLong())

        val (fecha, hora) = formatFechaHora(pago?.fechaPago)
        binding.tvFecha.text = fecha
        binding.tvHora.text  = hora

        binding.rvCuotas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCuotas.adapter = ReciboCuotaAdapter(pago?.abonos ?: emptyList())

        binding.ivClose.setOnClickListener { dismiss() }
        binding.btnListo.setOnClickListener { dismiss() }
    }

    /** "2026-06-15T14:30:00" -> ("15/06/2026", "2:30 pm"). Sin java.time para soportar minSdk 24. */
    private fun formatFechaHora(iso: String?): Pair<String, String> {
        if (iso.isNullOrBlank()) return Pair("-", "-")
        val partes = iso.split("T")
        val fecha = partes.getOrNull(0)?.split("-")?.let {
            if (it.size == 3) "${it[2]}/${it[1]}/${it[0]}" else partes[0]
        } ?: "-"
        val hora = partes.getOrNull(1)?.let { formatHora12(it) } ?: "-"
        return Pair(fecha, hora)
    }

    /** "14:30:00" -> "2:30 pm" */
    private fun formatHora12(time: String): String = try {
        val hh = time.substring(0, 2).toInt()
        val mm = time.substring(3, 5)
        val ampm = if (hh < 12) "am" else "pm"
        val h12 = when {
            hh == 0 -> 12
            hh > 12 -> hh - 12
            else -> hh
        }
        "$h12:$mm $ampm"
    } catch (e: Exception) {
        time
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            val ancho = (resources.displayMetrics.widthPixels * 0.88).toInt()
            setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
