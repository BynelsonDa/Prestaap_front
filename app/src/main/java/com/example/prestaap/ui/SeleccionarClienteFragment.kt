package com.example.prestaap.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestaap.R
import com.example.prestaap.UiState
import com.example.prestaap.databinding.FragmentSeleccionarClienteBinding
import com.example.prestaap.viewmodel.SeleccionarClienteViewModel

class SeleccionarClienteFragment : Fragment() {

    private var _binding: FragmentSeleccionarClienteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SeleccionarClienteViewModel by viewModels()
    private lateinit var adapter: SeleccionarClienteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeleccionarClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = SeleccionarClienteAdapter(emptyList()) { cliente ->
            val bundle = Bundle().apply {
                putLong("clienteCedula", cliente.cedula)
                putString("clienteNombre", cliente.nombre)
            }
            findNavController().navigate(R.id.action_seleccionarClienteFragment_to_crearCreditoFragment, bundle)
        }
        binding.rvClientes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClientes.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { viewModel.filtrar(s?.toString() ?: "") }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnRetry.setOnClickListener { viewModel.fetchClientes() }
    }

    private fun observeViewModel() {
        viewModel.clientes.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvClientes.visibility = View.GONE
                    binding.layoutError.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.layoutError.visibility = View.GONE
                    binding.rvClientes.visibility = View.VISIBLE
                    adapter.updateClientes(state.data)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvClientes.visibility = View.GONE
                    binding.layoutError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
