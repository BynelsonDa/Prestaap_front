package com.example.prestaap.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.prestaap.R
import com.example.prestaap.UiState
import com.example.prestaap.data.model.CrearCreditoRequest
import com.example.prestaap.data.model.FrecuenciaPago
import com.example.prestaap.databinding.FragmentCrearCreditoBinding
import com.example.prestaap.viewmodel.CrearCreditoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CrearCreditoFragment : Fragment() {

    private var _binding: FragmentCrearCreditoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CrearCreditoViewModel by viewModels()

    private var clienteCedula: Long = 0L
    private var clienteNombre: String = ""

    private var frecuencias: List<FrecuenciaPago> = emptyList()
    private var fechaLimiteCalendar: Calendar? = null

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateDisplayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrearCreditoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clienteCedula = arguments?.getLong("clienteCedula") ?: 0L
        clienteNombre = arguments?.getString("clienteNombre") ?: ""
        binding.tvClienteNombre.text = clienteNombre

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.layoutFechaLimite.setOnClickListener { showDatePicker() }
        binding.tvFechaLimite.setOnClickListener { showDatePicker() }

        binding.btnCrearCredito.setOnClickListener { validateAndSubmit() }
    }

    private fun showDatePicker() {
        val cal = fechaLimiteCalendar ?: Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                fechaLimiteCalendar = Calendar.getInstance().apply { set(year, month, day) }
                binding.tvFechaLimite.text = dateDisplayFormat.format(fechaLimiteCalendar!!.time)
                calcularCuotas()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun calcularCuotas() {
        val fechaLimite = fechaLimiteCalendar ?: return
        val frecuenciaPos = binding.spinnerFrecuencia.selectedItemPosition
        if (frecuenciaPos < 0 || frecuenciaPos >= frecuencias.size) return
        val dias = frecuencias[frecuenciaPos].dias
        if (dias <= 0) return

        val hoy = Calendar.getInstance()
        val diffMs = fechaLimite.timeInMillis - hoy.timeInMillis
        val diffDias = (diffMs / (1000L * 60 * 60 * 24)).toInt()
        val cuotas = if (diffDias > 0) diffDias / dias else 0
        binding.etCuotas.setText(cuotas.toString())
    }

    private fun validateAndSubmit() {
        val montoStr = binding.etMonto.text.toString().trim()
        val interesStr = binding.etInteres.text.toString().trim()
        val cuotasStr = binding.etCuotas.text.toString().trim()

        if (montoStr.isEmpty()) {
            binding.etMonto.error = "Ingresa el monto"
            return
        }
        if (interesStr.isEmpty()) {
            binding.etInteres.error = "Ingresa el interés"
            return
        }
        if (fechaLimiteCalendar == null) {
            Toast.makeText(requireContext(), "Selecciona la fecha límite", Toast.LENGTH_SHORT).show()
            return
        }
        if (cuotasStr.isEmpty() || cuotasStr == "0") {
            Toast.makeText(requireContext(), "La fecha límite no genera cuotas válidas", Toast.LENGTH_SHORT).show()
            return
        }

        val frecuenciaPos = binding.spinnerFrecuencia.selectedItemPosition
        if (frecuenciaPos < 0 || frecuenciaPos >= frecuencias.size) {
            Toast.makeText(requireContext(), "Selecciona la frecuencia de pago", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CrearCreditoRequest(
            montoPrestamo = montoStr.toDouble(),
            interes = interesStr.toDouble(),
            numeroDeCuotas = cuotasStr.toInt(),
            fechaPrestamo = dateFormat.format(Calendar.getInstance().time),
            fechaLimite = dateFormat.format(fechaLimiteCalendar!!.time),
            frecuenciaPagoId = frecuencias[frecuenciaPos].id,
            clienteCedula = clienteCedula
        )

        viewModel.crearCredito(request)
    }

    private fun observeViewModel() {
        viewModel.frecuencias.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressFrecuencias.visibility = View.VISIBLE
                    binding.spinnerFrecuencia.visibility = View.INVISIBLE
                }
                is UiState.Success -> {
                    frecuencias = state.data
                    val nombres = frecuencias.map { it.nombre }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_spinner_frecuencia,
                        nombres
                    )
                    adapter.setDropDownViewResource(R.layout.item_spinner_frecuencia)
                    binding.spinnerFrecuencia.adapter = adapter
                    binding.progressFrecuencias.visibility = View.GONE
                    binding.spinnerFrecuencia.visibility = View.VISIBLE
                }
                is UiState.Error -> {
                    binding.progressFrecuencias.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error cargando frecuencias: ${state.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.crearState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnCrearCredito.isEnabled = false
                }
                is UiState.Success -> {
                    binding.btnCrearCredito.isEnabled = true
                    Toast.makeText(requireContext(), "Crédito creado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    findNavController().navigateUp()
                }
                is UiState.Error -> {
                    binding.btnCrearCredito.isEnabled = true
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
