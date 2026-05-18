package com.example.prestaap.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prestaap.R
import com.example.prestaap.UiState
import com.example.prestaap.databinding.FragmentZonasBinding
import com.example.prestaap.viewmodel.ZonasViewModel

class ZonasFragment : Fragment() {

    private var _binding: FragmentZonasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ZonasViewModel by viewModels()
    private lateinit var adapter: ZonasAdapter

    private var fabMenuOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentZonasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupFab()
        observeViewModel()
        setupBottomNav()
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_clientes
    }

    private fun setupRecyclerView() {
        adapter = ZonasAdapter(
            zonas = emptyList(),
            onZonaClick = {
                findNavController().navigate(R.id.action_zonas_to_clientes)
            },
            onAddClick = {
                // TODO: navigate to CreateZonaFragment
            }
        )
        binding.rvZonas.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvZonas.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { viewModel.filtrar(s?.toString() ?: "") }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupFab() {
        binding.fabMain.setOnClickListener { toggleFabMenu(true) }
        binding.fabClose.setOnClickListener { toggleFabMenu(false) }
        binding.fabOverlay.setOnClickListener { toggleFabMenu(false) }
        binding.btnCrearCredito.setOnClickListener {
            toggleFabMenu(false)
            findNavController().navigate(R.id.action_global_seleccionarClienteFragment)
        }
        binding.btnCrearCliente.setOnClickListener {
            toggleFabMenu(false)
            findNavController().navigate(R.id.action_global_nuevoClienteFragment)
        }
    }

    private fun toggleFabMenu(open: Boolean) {
        fabMenuOpen = open
        if (open) {
            binding.fabOverlay.visibility = View.VISIBLE
            binding.fabOverlay.animate().alpha(1f).setDuration(200).start()
            binding.fabMain.hide()
        } else {
            // Capture direct View reference — the lambda must NOT access `binding`
            // because onDestroyView nulls _binding before the 200ms animation ends
            val overlay = binding.fabOverlay
            overlay.animate().alpha(0f).setDuration(200).withEndAction {
                overlay.visibility = View.GONE
            }.start()
            binding.fabMain.show()
        }
    }

    private fun observeViewModel() {
        viewModel.zonas.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvZonas.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvZonas.visibility = View.VISIBLE
                    adapter.updateZonas(state.data)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvZonas.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding?.fabOverlay?.animate()?.cancel()
        super.onDestroyView()
        _binding = null
    }
}
