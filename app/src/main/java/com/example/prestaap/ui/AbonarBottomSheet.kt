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
import androidx.fragment.app.viewModels
import com.example.prestaap.UiState
import com.example.prestaap.data.model.AbonoCreditoRequest
import com.example.prestaap.data.model.CreditoResumen
import com.example.prestaap.databinding.BottomSheetAbonarBinding
import com.example.prestaap.viewmodel.AbonarViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AbonarBottomSheet : DialogFragment() {

    private var _binding: BottomSheetAbonarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AbonarViewModel by viewModels()

    // Bancos para "Transferencia" (aún no respaldados en el backend)
    private val bancos = listOf("Nequi", "Daviplata", "Bancolombia", "Davivienda")
    private var bancoSeleccionado: String? = null

    private var creditoId: Int? = null
    private var creditoLabel: String = "Crédito"
    // Cuando se abre desde "Abonar a todo", lleva la lista de créditos del cliente.
    private var creditosTodos: List<CreditoResumen>? = null

    private val fechaCalendar: Calendar = Calendar.getInstance()
    private val dateDisplayFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    companion object {
        // Único método respaldado por ahora: Efectivo (id=1 en metodo_de_pago)
        private const val METODO_EFECTIVO_ID = 1

        fun newInstance(creditoId: Int?, creditoLabel: String? = null) = AbonarBottomSheet().apply {
            arguments = Bundle().apply {
                if (creditoId != null) putInt("creditoId", creditoId)
                if (creditoLabel != null) putString("creditoLabel", creditoLabel)
            }
        }

        /** "Abonar a todo": lleva todos los créditos del cliente para repartir el monto. */
        fun newInstanceTodos(creditos: List<CreditoResumen>) = AbonarBottomSheet().apply {
            arguments = Bundle().apply {
                putString("creditosTodosJson", Gson().toJson(creditos))
                putString("creditoLabel", "Todos")
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

        creditoId = arguments?.let { if (it.containsKey("creditoId")) it.getInt("creditoId") else null }
        creditoLabel = arguments?.getString("creditoLabel")
            ?: creditoId?.let { "Crédito #$it" }
            ?: "Todos"
        binding.tvCredito.text = creditoLabel

        arguments?.getString("creditosTodosJson")?.let { json ->
            val type = object : TypeToken<List<CreditoResumen>>() {}.type
            creditosTodos = Gson().fromJson(json, type)
        }

        binding.tvFecha.text = dateDisplayFormat.format(fechaCalendar.time)

        binding.ivClose.setOnClickListener { dismiss() }
        binding.layoutFecha.setOnClickListener { mostrarDatePicker() }

        binding.cbFisico.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbTransferencia.isChecked = false
                binding.layoutBanco.visibility = View.GONE
                bancoSeleccionado = null
            }
        }
        binding.cbTransferencia.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbFisico.isChecked = false
                binding.layoutBanco.visibility = View.VISIBLE
            } else {
                binding.layoutBanco.visibility = View.GONE
            }
        }
        binding.layoutBanco.setOnClickListener { mostrarBancos() }
        binding.btnConfirmar.setOnClickListener { registrar() }

        observarRegistro()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            val ancho = (resources.displayMetrics.widthPixels * 0.88).toInt()
            setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
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

    private fun mostrarBancos() {
        val popup = PopupMenu(requireContext(), binding.layoutBanco)
        bancos.forEachIndexed { index, banco ->
            popup.menu.add(0, index, index, banco)
        }
        popup.setOnMenuItemClickListener { item ->
            bancoSeleccionado = bancos[item.itemId]
            binding.tvBanco.text = bancoSeleccionado
            binding.tvBanco.setTextColor(Color.parseColor("#212121"))
            true
        }
        popup.show()
    }

    private fun registrar() {
        // Por ahora solo "Físico" (Efectivo) está respaldado en el backend
        if (binding.cbTransferencia.isChecked) {
            Toast.makeText(
                requireContext(),
                "Transferencia aún no disponible (solo efectivo por ahora)",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (!binding.cbFisico.isChecked) {
            Toast.makeText(requireContext(), "Selecciona un método de pago", Toast.LENGTH_SHORT).show()
            return
        }
        val montoStr = binding.etMonto.text.toString().trim()
        if (montoStr.isEmpty()) {
            binding.etMonto.error = "Ingresa el monto"
            return
        }
        val monto = montoStr.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            binding.etMonto.error = "Monto inválido"
            return
        }
        val fechaPago = isoFormat.format(fechaCalendar.time)

        // Modo "Abonar a todo": reparte el monto entre los créditos del cliente
        val todos = creditosTodos
        if (todos != null) {
            if (todos.isEmpty()) {
                Toast.makeText(requireContext(), "El cliente no tiene créditos", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.registrarPagoTodos(todos, monto, fechaPago, METODO_EFECTIVO_ID)
            return
        }

        val cid = creditoId
        if (cid == null || cid <= 0) {
            Toast.makeText(requireContext(), "Selecciona un crédito para abonar", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.registrarPago(
            cid,
            AbonoCreditoRequest(monto, fechaPago, METODO_EFECTIVO_ID, null)
        )
    }

    private fun observarRegistro() {
        viewModel.pago.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> binding.btnConfirmar.isEnabled = false
                is UiState.Success -> {
                    val json = Gson().toJson(state.data)
                    ReciboPagoDialog.newInstance(json, creditoLabel)
                        .show(parentFragmentManager, "recibo_pago")
                    dismiss()
                }
                is UiState.Error -> {
                    binding.btnConfirmar.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
