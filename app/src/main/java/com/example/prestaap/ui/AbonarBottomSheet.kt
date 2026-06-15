package com.example.prestaap.ui

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.prestaap.databinding.BottomSheetAbonarBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AbonarBottomSheet : DialogFragment() {

    private var _binding: BottomSheetAbonarBinding? = null
    private val binding get() = _binding!!

    // Métodos de pago fijos (aún no hay endpoint en el backend)
    private val metodosPago = listOf("Efectivo", "Transferencia", "Nequi", "Daviplata")
    private var metodoSeleccionado: String? = null

    private val fechaCalendar: Calendar = Calendar.getInstance()
    private val dateDisplayFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

    companion object {
        fun newInstance(creditoId: Int?, creditoLabel: String? = null) = AbonarBottomSheet().apply {
            arguments = Bundle().apply {
                if (creditoId != null) putInt("creditoId", creditoId)
                if (creditoLabel != null) putString("creditoLabel", creditoLabel)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAbonarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crédito auto-seleccionado: muestra desde cuál se abrió el sheet
        val creditoLabel = arguments?.getString("creditoLabel")
        val creditoId = arguments?.let { if (it.containsKey("creditoId")) it.getInt("creditoId") else null }
        binding.tvCredito.text = creditoLabel
            ?: creditoId?.let { "Crédito #$it" }
            ?: "Todos los créditos"

        // Fecha de hoy por defecto
        binding.tvFecha.text = dateDisplayFormat.format(fechaCalendar.time)

        binding.ivClose.setOnClickListener { dismiss() }
        binding.layoutFecha.setOnClickListener { mostrarDatePicker() }
        binding.layoutMetodo.setOnClickListener { mostrarMetodos() }
        binding.btnConfirmar.setOnClickListener { registrar() }
    }

    private fun mostrarDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                fechaCalendar.set(year, month, day)
                binding.tvFecha.text = dateDisplayFormat.format(fechaCalendar.time)
            },
            fechaCalendar.get(Calendar.YEAR),
            fechaCalendar.get(Calendar.MONTH),
            fechaCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun mostrarMetodos() {
        val popup = PopupMenu(requireContext(), binding.layoutMetodo)
        metodosPago.forEachIndexed { index, metodo ->
            popup.menu.add(0, index, index, metodo)
        }
        popup.setOnMenuItemClickListener { item ->
            metodoSeleccionado = metodosPago[item.itemId]
            binding.tvMetodoPago.text = metodoSeleccionado
            binding.tvMetodoPago.setTextColor(Color.parseColor("#212121"))
            true
        }
        popup.show()
    }

    private fun registrar() {
        if (metodoSeleccionado == null) {
            Toast.makeText(requireContext(), "Selecciona un método de pago", Toast.LENGTH_SHORT).show()
            return
        }
        val monto = binding.etMonto.text.toString().trim()
        if (monto.isEmpty()) {
            binding.etMonto.error = "Ingresa el monto"
            return
        }
        // Solo diseño: por ahora no se envía al backend
        Toast.makeText(
            requireContext(),
            "Abono de $$monto ($metodoSeleccionado) registrado",
            Toast.LENGTH_SHORT
        ).show()
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        // Ventana emergente centrada: fondo transparente para que se vea la tarjeta
        // redondeada, y ancho al 88% de la pantalla.
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
