package com.example.prestaap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestaap.UiState
import com.example.prestaap.databinding.FragmentHistorialPagosBinding
import com.example.prestaap.viewmodel.HistorialPagosViewModel

class HistorialPagosFragment : Fragment() {

    private var _binding: FragmentHistorialPagosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistorialPagosViewModel by viewModels()
    private lateinit var adapter: HistorialPagosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialPagosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cedula = arguments?.getLong("cedula") ?: 0L
        val nombreCliente = arguments?.getString("nombreCliente") ?: "Cliente"

        binding.tvNombreCliente.text = nombreCliente
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setupRecyclerView()
        observeViewModel()

        viewModel.cargarHistorial(cedula)
    }

    private fun setupRecyclerView() {
        adapter = HistorialPagosAdapter { pago ->
            PagoDetalleBottomSheet.newInstance(
                numeroCredito = pago.numeroCredito,
                numeroCuota = pago.numeroCuota,
                capitalAbonado = pago.capitalAbonado,
                interesAbonado = pago.interesAbonado,
                montoTotal = pago.montoTotal
            ).show(parentFragmentManager, "pago_detalle")
        }
        binding.rvPagos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPagos.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.pagosState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvPagos.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = state.data
                    if (data.isEmpty()) {
                        binding.rvPagos.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.rvPagos.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                        adapter.submitList(data)
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvPagos.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
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
