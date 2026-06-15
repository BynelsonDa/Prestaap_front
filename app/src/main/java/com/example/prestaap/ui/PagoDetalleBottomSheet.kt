package com.example.prestaap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prestaap.databinding.BottomSheetPagoDetalleBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PagoDetalleBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPagoDetalleBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(
            numeroCredito: Int,
            numeroCuota: Int,
            capitalAbonado: Double,
            interesAbonado: Double,
            montoTotal: Double
        ) = PagoDetalleBottomSheet().apply {
            arguments = Bundle().apply {
                putInt("numeroCredito", numeroCredito)
                putInt("numeroCuota", numeroCuota)
                putDouble("capitalAbonado", capitalAbonado)
                putDouble("interesAbonado", interesAbonado)
                putDouble("montoTotal", montoTotal)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPagoDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val numCredito = args.getInt("numeroCredito")
        val numCuota = args.getInt("numeroCuota")
        val capAbonado = args.getDouble("capitalAbonado")
        val intAbonado = args.getDouble("interesAbonado")
        val total = args.getDouble("montoTotal")

        binding.tvNumCredito.text = "#$numCredito"
        binding.tvNumCuota.text = numCuota.toString()
        binding.tvCapitalAbonado.text = formatPeso(capAbonado.toLong())
        binding.tvInteresAbonado.text = formatPeso(intAbonado.toLong())
        binding.tvMontoTotal.text = formatPeso(total.toLong())

        binding.btnCerrar.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
