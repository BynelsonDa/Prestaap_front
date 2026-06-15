package com.example.prestaap.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestaap.R
import com.example.prestaap.UiState
import com.example.prestaap.databinding.FragmentClientesBinding
import com.example.prestaap.viewmodel.ClientesViewModel

class ClientesFragment : Fragment() {

    private var _binding: FragmentClientesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClientesViewModel by viewModels()
    private lateinit var adapter: ClientesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupSpinner()
        setupFab()
        observeViewModel()
    binding.bottomNav.selectedItemId = R.id.nav_clientes
    binding.bottomNav.setOnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_inicio -> {
                findNavController().navigate(R.id.zonasFragment)
                true
            }

            R.id.nav_clientes -> true

            R.id.nav_resumen -> {
                findNavController().navigate(R.id.resumenFragment)
                true
            }

            else -> true
        }
    }

        val zonaId = arguments?.getInt("zonaId") ?: 0
        Log.d("ClientesFrag", "arguments=$arguments  zonaId=$zonaId")
        viewModel.fetchClientes(zonaId)
    }

    private fun setupRecyclerView() {
        adapter = ClientesAdapter { cliente ->
            val bundle = Bundle().apply {
                putString("nombreCliente", cliente.nombre)
                putInt("clienteId", cliente.cedula.toInt())
                putLong("cedula", cliente.cedula)
                putString("direccion", cliente.direccion)
                putLong("montoTotal", cliente.montoTotalPrestamo.toLong())
                putInt("creditosPrestados", cliente.cantidadCreditos)
            }
            findNavController().navigate(R.id.action_clientesFragment_to_clienteCreditosFragment, bundle)
        }
        binding.rvClientes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClientes.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.filtrarPorNombre(s?.toString() ?: "")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupSpinner() {
        val estados = listOf("Todos", "Pendiente", "Pagado")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = spinnerAdapter

        binding.spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.filtrarPorEstado(estados[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupFab() {
        binding.fabMain.setOnClickListener {
            // TODO: show Crear Crédito / Crear Cliente options
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.clientes.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(state.data)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
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
