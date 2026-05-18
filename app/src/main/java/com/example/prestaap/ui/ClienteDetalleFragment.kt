package com.example.prestaap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.prestaap.databinding.FragmentClienteDetalleBinding
import com.example.prestaap.databinding.ItemInfoRowBinding
import com.example.prestaap.ui.formatPeso

class ClienteDetalleFragment : Fragment() {

    private var _binding: FragmentClienteDetalleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClienteDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val nombre           = args.getString("nombre", "")
        val cedula           = args.getString("cedula", "")
        val telefono         = args.getString("telefono", "")
        val direccion        = args.getString("direccion", "")
        val montoTotal       = args.getLong("montoTotal", 0L)
        val creditosPrestados = args.getInt("creditosPrestados", 0)

        binding.tvNombreHeader.text = nombre

        bindRow(binding.rowCedula,    "Cédula:",               cedula)
        bindRow(binding.rowTelefono,  "Teléfono:",             telefono)
        bindRow(binding.rowDireccion, "Dirección:",            direccion)
        bindRow(binding.rowMonto,     "Monto total prestado:", formatPeso(montoTotal))
        bindRow(binding.rowCreditos,  "Créditos prestados:",   "$creditosPrestados créditos")

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun bindRow(row: ItemInfoRowBinding, label: String, value: String) {
        row.tvLabel.text = label
        row.tvValue.text = value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
