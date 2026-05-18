package com.example.prestaap.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prestaap.R
import com.example.prestaap.UiState
import com.example.prestaap.databinding.FragmentClientesBinding
import com.example.prestaap.viewmodel.ClientesViewModel

class ClientesFragment : Fragment() {

    private var _binding: FragmentClientesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClientesViewModel by viewModels()
    private lateinit var adapter: ZonasAdapter

    private var fabMenuOpen = false

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
        setupFab()
        observeViewModel()
        binding.bottomNav.selectedItemId = R.id.nav_clientes
    }

    private fun setupRecyclerView() {
        adapter = ZonasAdapter(
            zonas = emptyList(),
            onZonaClick = { zona ->
                // TODO: navigate to ClientesDeZonaFragment with zona.id
            },
            onAddClick = {
                // TODO: navigate to CreateZonaFragment
            }
        )
        binding.rvZonas.layoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) = 1
            }
        }
        binding.rvZonas.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.cargarZonas(s?.toString() ?: "")
            }
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
            // TODO: navigate to CreateCreditoFragment
        }
        binding.btnCrearCliente.setOnClickListener {
            toggleFabMenu(false)
            // TODO: navigate to CreateClienteFragment
        }
    }

    private fun toggleFabMenu(open: Boolean) {
        fabMenuOpen = open
        if (open) {
            binding.fabOverlay.visibility = View.VISIBLE
            binding.fabOverlay.animate().alpha(1f).setDuration(200).start()
            binding.fabMain.hide()
        } else {
            binding.fabOverlay.animate().alpha(0f).setDuration(200).withEndAction {
                binding.fabOverlay.visibility = View.GONE
            }.start()
            binding.fabMain.show()
        }
    }

    private fun observeViewModel() {
        viewModel.zonas.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> { /* TODO: mostrar shimmer/spinner */ }
                is UiState.Success -> adapter.updateZonas(state.data)
                is UiState.Error   -> { /* TODO: mostrar snackbar de error */ }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
