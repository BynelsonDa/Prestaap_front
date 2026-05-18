package com.example.prestaap.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestaap.R
import com.example.prestaap.data.model.Credito
import com.example.prestaap.databinding.FragmentClienteCreditosBinding
import com.google.android.material.button.MaterialButton

class ClienteCreditosFragment : Fragment() {

    private var _binding: FragmentClienteCreditosBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CreditosAdapter
    private var showingAll = true
    private var currentEstado = "Todos"

    private val mockCreditos = listOf(
        Credito(1, "Crédito 1", "Pendiente", 80000,  48000,  2, 4,
                "01/10/2025", "Semanal", 20.0, 4000L,  20000L, 1, 48000L,  48000L, "29/10/2025"),
        Credito(2, "Crédito 2", "Atrasado",  120000, 96000,  2, 5,
                "11/12/2025", "Semanal", 20.0, 24000L, 24000L, 2, 48000L,  96000L, "15/01/2026"),
        Credito(3, "Crédito 3", "Pagado",    80000,  0,      4, 4,
                "01/06/2025", "Mensual", 20.0, 4000L,  20000L, 0, 96000L,  0L,     "01/10/2025"),
        Credito(4, "Crédito 4", "Pagado",    80000,  0,      4, 4,
                "15/07/2025", "Mensual", 20.0, 4000L,  20000L, 0, 96000L,  0L,     "15/11/2025")
    )

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
    }

    private fun setupRecyclerView() {
        adapter = CreditosAdapter(
            onAbonarClick = { credito ->
                AbonarBottomSheet.newInstance(credito.id)
                    .show(parentFragmentManager, "abonar")
            },
            onCreditoClick = { credito ->
                val bundle = Bundle().apply {
                    putInt("creditoId",           credito.id)
                    putString("nombre",           credito.nombre)
                    putString("estado",           credito.estado)
                    putLong("prestado",           credito.prestado)
                    putLong("restante",           credito.restante)
                    putInt("cuotasPagadas",       credito.cuotasPagadas)
                    putInt("totalCuotas",         credito.totalCuotas)
                    putString("fechaCredito",     credito.fechaCredito)
                    putString("frecuenciaPago",   credito.frecuenciaPago)
                    putFloat("porcentajeInteres", credito.porcentajeInteres.toFloat())
                    putLong("interesesPorCuota",  credito.interesesPorCuota)
                    putLong("valorCuotaCapital",  credito.valorCuotaCapital)
                    putInt("cuotasVencidas",      credito.cuotasVencidas)
                    putLong("totalAbonado",       credito.totalAbonado)
                    putLong("deudaTotal",         credito.deudaTotal)
                    putString("fechaVencimiento", credito.fechaVencimiento)
                }
                findNavController().navigate(
                    R.id.action_clienteCreditosFragment_to_creditoDetalleFragment,
                    bundle
                )
            }
        )
        binding.rvCreditos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCreditos.adapter = adapter
        applyFilter()
    }

    private fun setupTabs() {
        setTabActive(binding.btnTodosLosCreditos, binding.btnACobrarHoy)

        binding.btnTodosLosCreditos.setOnClickListener {
            showingAll = true
            setTabActive(binding.btnTodosLosCreditos, binding.btnACobrarHoy)
            applyFilter()
        }
        binding.btnACobrarHoy.setOnClickListener {
            showingAll = false
            setTabActive(binding.btnACobrarHoy, binding.btnTodosLosCreditos)
            applyFilter()
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
        val estados = listOf("Todos", "Pendiente", "Atrasado", "Pagado")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = spinnerAdapter

        binding.spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                currentEstado = estados[position]
                applyFilter()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun applyFilter() {
        var list = if (showingAll) mockCreditos
                   else mockCreditos.filter { it.estado == "Pendiente" || it.estado == "Atrasado" }
        if (currentEstado != "Todos") list = list.filter { it.estado == currentEstado }
        adapter.submitList(list)
    }

    private fun setupInfoPersonalButton(args: Bundle) {
        binding.btnInfoPersonal.setOnClickListener {
            val bundle = Bundle().apply {
                putString("nombre",    args.getString("nombreCliente", ""))
                putString("cedula",    args.getString("cedula", ""))
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
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Eliminar crédito?")
                .setMessage("Esta acción no se puede deshacer")
                .setPositiveButton("Eliminar") { _, _ -> }
                .setNegativeButton("Cancelar", null)
                .show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#E53935"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
