package com.example.prestaap.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.prestaap.UiState
import com.example.prestaap.data.model.CreditoDetalle
import com.example.prestaap.databinding.FragmentCreditoDetalleBinding
import com.example.prestaap.viewmodel.CreditoDetalleViewModel

class CreditoDetalleFragment : Fragment() {

    private var _binding: FragmentCreditoDetalleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreditoDetalleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditoDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val creditoId = args.getInt("creditoId", 0)
        val nombre    = args.getString("nombre", "Crédito")

        binding.tvCreditoNombre.text = nombre
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnAbonar.setOnClickListener {
            AbonarBottomSheet.newInstance(creditoId, nombre).show(parentFragmentManager, "abonar_detalle")
        }

        observeViewModel()
        viewModel.fetchDetalle(creditoId)
    }

    private fun observeViewModel() {
        viewModel.detalle.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility  = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility  = View.VISIBLE
                    bindDetalle(state.data)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility  = View.VISIBLE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun bindDetalle(d: CreditoDetalle) {
        binding.tvTotalPrestado.text     = formatPeso(d.totalPrestado.toLong())
        binding.tvFechaCredito.text      = d.fechaPrestamo
        binding.tvFrecuenciaPago.text    = d.frecuenciaPago
        binding.tvTotalCuotas.text       = d.numeroDeCuotas.toString()
        binding.tvPorcentajeInteres.text = "${d.interes}%"
        binding.tvInteresesPorCuota.text = formatPeso(d.valorInteresPorCuota.toLong())
        binding.tvCuotasPagadas.text     = "${d.cuotasPagadas}/${d.numeroDeCuotas}"
        binding.tvCuotasVencidas.text    = d.cuotasVencidas.toString()
        binding.tvTotalAbonado.text      = formatPeso(d.totalAbonado.toLong())
        binding.tvDeudaTotal.text        = formatPeso(d.deudaTotal.toLong())
        binding.tvFechaVencimiento.text  = d.fechaLimite
        applyBadge(d.estado)
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
