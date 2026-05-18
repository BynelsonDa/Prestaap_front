package com.example.prestaap.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.prestaap.databinding.FragmentCreditoDetalleBinding

class CreditoDetalleFragment : Fragment() {

    private var _binding: FragmentCreditoDetalleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditoDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val creditoId         = args.getInt("creditoId", 0)
        val nombre            = args.getString("nombre", "Crédito")
        val estado            = args.getString("estado", "")
        val prestado          = args.getLong("prestado", 0L)
        val cuotasPagadas     = args.getInt("cuotasPagadas", 0)
        val totalCuotas       = args.getInt("totalCuotas", 0)
        val fechaCredito      = args.getString("fechaCredito", "")
        val frecuenciaPago    = args.getString("frecuenciaPago", "")
        val porcentajeInteres = args.getFloat("porcentajeInteres", 0f)
        val interesesPorCuota = args.getLong("interesesPorCuota", 0L)
        val valorCuotaCapital = args.getLong("valorCuotaCapital", 0L)
        val cuotasVencidas    = args.getInt("cuotasVencidas", 0)
        val totalAbonado      = args.getLong("totalAbonado", 0L)
        val deudaTotal        = args.getLong("deudaTotal", 0L)
        val fechaVencimiento  = args.getString("fechaVencimiento", "")

        binding.tvCreditoNombre.text     = nombre
        binding.tvTotalPrestado.text     = formatPeso(prestado)
        binding.tvFechaCredito.text      = fechaCredito
        binding.tvFrecuenciaPago.text    = frecuenciaPago
        binding.tvTotalCuotas.text       = totalCuotas.toString()
        binding.tvPorcentajeInteres.text = "${porcentajeInteres.toInt()}%"
        binding.tvInteresesPorCuota.text = formatPeso(interesesPorCuota)
        binding.tvCuotasPagadas.text     = "$cuotasPagadas/$totalCuotas"
        binding.tvValorCuotaCapital.text = formatPeso(valorCuotaCapital)
        binding.tvCuotasVencidas.text    = cuotasVencidas.toString()
        binding.tvTotalAbonado.text      = formatPeso(totalAbonado)
        binding.tvDeudaTotal.text        = formatPeso(deudaTotal)
        binding.tvFechaVencimiento.text  = fechaVencimiento

        applyBadge(estado)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnAbonar.setOnClickListener {
            AbonarBottomSheet.newInstance(creditoId).show(parentFragmentManager, "abonar_detalle")
        }
    }

    private fun applyBadge(estado: String) {
        val (bgColor, textColor) = when (estado) {
            "Pendiente" -> Pair("#FFF3CD", "#856404")
            "Atrasado"  -> Pair("#FFEBEE", "#C62828")
            "Pagado"    -> Pair("#E8F5E9", "#2E7D32")
            else        -> Pair("#FFF3CD", "#856404")
        }
        val density = binding.root.context.resources.displayMetrics.density
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor(bgColor))
            cornerRadius = 12 * density
        }
        binding.tvEstado.background = drawable
        binding.tvEstado.setTextColor(Color.parseColor(textColor))
        binding.tvEstado.text = estado
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
