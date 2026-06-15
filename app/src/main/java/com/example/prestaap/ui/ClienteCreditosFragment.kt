package com.example.prestaap.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestaap.R
import com.example.prestaap.UiState
import com.example.prestaap.databinding.FragmentClienteCreditosBinding
import com.example.prestaap.viewmodel.ClienteCreditosViewModel
import com.google.android.material.button.MaterialButton

class ClienteCreditosFragment : Fragment() {

    private var _binding: FragmentClienteCreditosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClienteCreditosViewModel by viewModels()
    private lateinit var adapter: CreditosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClienteCreditosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        binding.tvClienteNombre.text = args.getString("nombreCliente", "Cliente")
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        setupRecyclerView()
        setupTabs()
        setupSpinner()
        setupInfoPersonalButton(args)
        setupBottomButtons()
        observeViewModel()

        val cedula = args.getLong("cedula", 0L)
        viewModel.fetchCreditos(cedula)
    }

    private fun setupRecyclerView() {
        adapter = CreditosAdapter(
            onAbonarClick = { credito, position ->
                AbonarBottomSheet.newInstance(position, credito.label)
                    .show(parentFragmentManager, "abonar")
            },
            onCreditoClick = { credito, position ->
                val bundle = Bundle().apply {
                    putInt("creditoId",     position + 1)
                    putString("nombre",     credito.label)
                    putString("estado",     credito.estado)
                    putLong("prestado",     credito.montoPrestamo.toLong())
                    putLong("restante",     credito.montoRestante.toLong())
                    putInt("cuotasPagadas", credito.cuotasPagadas)
                    putInt("totalCuotas",   credito.totalCuotas)
                }
                findNavController().navigate(
                    R.id.action_clienteCreditosFragment_to_creditoDetalleFragment,
                    bundle
                )
            }
        )
        binding.rvCreditos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCreditos.adapter = adapter
    }

    private fun setupTabs() {
        setTabActive(binding.btnTodosLosCreditos, binding.btnACobrarHoy)

        binding.btnTodosLosCreditos.setOnClickListener {
            setTabActive(binding.btnTodosLosCreditos, binding.btnACobrarHoy)
            viewModel.mostrarTodos()
        }
        binding.btnACobrarHoy.setOnClickListener {
            setTabActive(binding.btnACobrarHoy, binding.btnTodosLosCreditos)
            viewModel.mostrarACobrar()
        }
    }

    private fun setTabActive(active: MaterialButton, inactive: MaterialButton) {
        active.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2563EB"))
        active.setTextColor(Color.WHITE)
        active.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT))

        inactive.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        inactive.setTextColor(Color.parseColor("#333333"))
        inactive.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#CCCCCC")))
    }

    private fun setupSpinner() {
        val estados = listOf("Todos", "Pendiente", "Pagado")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = spinnerAdapter

        binding.spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                viewModel.filtrarPorEstado(estados[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observeViewModel() {
        viewModel.creditos.observe(viewLifecycleOwner) { state ->
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

    private fun setupInfoPersonalButton(args: Bundle) {
        binding.btnInfoPersonal.setOnClickListener {
            val bundle = Bundle().apply {
                putString("nombre",    args.getString("nombreCliente", ""))
                putString("cedula",    args.getLong("cedula", 0L).toString())
                putString("telefono",  args.getString("telefono", ""))
                putString("direccion", args.getString("direccion", ""))
                putLong("montoTotal",  args.getLong("montoTotal", 0L))
                putInt("creditosPrestados", args.getInt("creditosPrestados", 0))
            }
            findNavController().navigate(
                R.id.action_clienteCreditosFragment_to_clienteDetalleFragment,
                bundle
            )
        }
    }

    private fun setupBottomButtons() {
        binding.btnAbonarTodo.setOnClickListener {
            AbonarBottomSheet.newInstance(null).show(parentFragmentManager, "abonar_todo")
        }
        binding.btnEliminar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("¿Eliminar crédito?")
                .setMessage("Esta acción no se puede deshacer")
                .setPositiveButton("Eliminar") { _, _ -> }
                .setNegativeButton("Cancelar", null)
                .show()
                .also { dialog ->
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        ?.setTextColor(Color.parseColor("#E53935"))
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
