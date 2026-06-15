package com.example.prestaap.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestaap.R
import com.example.prestaap.UiState
import com.example.prestaap.databinding.FragmentResumenBinding
import com.example.prestaap.viewmodel.ResumenViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ResumenFragment : Fragment() {

    private var _binding: FragmentResumenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResumenViewModel by viewModels()
    private lateinit var adapter: MetodosPagoAdapter

    private val calendarInicio: Calendar = Calendar.getInstance()
    private val calendarFin: Calendar = Calendar.getInstance()

    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNav()
        setupRecyclerView()
        setupDatePickers()
        setupButtons()
        observeViewModel()

        actualizarResumen()
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_resumen

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    findNavController().navigate(R.id.zonasFragment)
                    true
                }

                R.id.nav_clientes -> {
                    findNavController().navigate(R.id.clientesFragment)
                    true
                }

                R.id.nav_resumen -> true

                else -> true
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = MetodosPagoAdapter()
        binding.rvMetodosPago.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMetodosPago.adapter = adapter
    }

    private fun setupDatePickers() {
        binding.tvFechaInicio.text = displayFormat.format(calendarInicio.time)
        binding.tvFechaFin.text = displayFormat.format(calendarFin.time)

        binding.btnFechaInicio.setOnClickListener {
            mostrarDatePicker(calendarInicio) {
                binding.tvFechaInicio.text = displayFormat.format(calendarInicio.time)
                actualizarResumen()
            }
        }

        binding.btnFechaFin.setOnClickListener {
            mostrarDatePicker(calendarFin) {
                binding.tvFechaFin.text = displayFormat.format(calendarFin.time)
                actualizarResumen()
            }
        }
    }

    private fun mostrarDatePicker(
        calendar: Calendar,
        onDateSelected: () -> Unit,
    ) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onDateSelected()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    private fun setupButtons() {
        binding.btnPdf.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Generación de PDF próximamente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun actualizarResumen() {
        val fechaInicio = apiFormat.format(calendarInicio.time)
        val fechaFin = apiFormat.format(calendarFin.time)
        viewModel.cargarResumen(fechaInicio, fechaFin)
    }

    private fun observeViewModel() {
        viewModel.resumenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    val data = state.data

                    binding.tvTotalPrestado.text = formatPeso(data.totalPrestado.toLong())
                    binding.tvCapitalPrestado.text = formatPeso(data.capitalPrestado.toLong())
                    binding.tvInteresesPrestados.text = formatPeso(data.interesesPrestados.toLong())

                    binding.tvTotalRecibido.text = formatPeso(data.totalRecibido.toLong())
                    binding.tvCapitalRecibido.text = formatPeso(data.capitalRecibido.toLong())
                    binding.tvInteresesRecibidos.text = formatPeso(data.interesesRecibidos.toLong())

                    adapter.submitList(data.metodosPago)
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
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