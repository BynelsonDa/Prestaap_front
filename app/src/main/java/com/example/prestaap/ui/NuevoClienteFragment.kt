package com.example.prestaap.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.prestaap.R
import com.example.prestaap.UiState
import com.example.prestaap.data.model.NuevoClienteRequest
import com.example.prestaap.data.model.ReferenciaRequest
import com.example.prestaap.data.model.Zona
import com.example.prestaap.databinding.FragmentNuevoClienteBinding
import com.example.prestaap.viewmodel.NuevoClienteViewModel

class NuevoClienteFragment : Fragment() {

    private var _binding: FragmentNuevoClienteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NuevoClienteViewModel by viewModels()
    private var zonas: List<Zona> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevoClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        setupSwitch()
        setupButton()
        observeViewModel()
    }

    private fun setupSwitch() {
        binding.switchReferencia.apply {
            thumbTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                intArrayOf(Color.parseColor("#2563EB"), Color.parseColor("#CCCCCC"))
            )
            trackTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                intArrayOf(Color.parseColor("#90B4F5"), Color.parseColor("#E0E0E0"))
            )
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.layoutReferencia.visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
                    binding.layoutReferencia.startAnimation(animation)
                } else {
                    binding.layoutReferencia.visibility = View.GONE
                }
            }
        }
    }

    private fun setupButton() {
        binding.btnGuardar.setOnClickListener { validateAndSave() }
    }

    private fun observeViewModel() {
        viewModel.zonas.observe(viewLifecycleOwner) { list ->
            zonas = list
            val items = listOf("Seleccionar zona") + list.map { it.nombre }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerZona.adapter = adapter
        }

        viewModel.guardadoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> binding.btnGuardar.isEnabled = false
                is UiState.Success -> {
                    binding.btnGuardar.isEnabled = true
                    Toast.makeText(requireContext(), "Cliente guardado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is UiState.Error -> {
                    binding.btnGuardar.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validateAndSave() {
        val nombre = binding.etNombre.text.toString().trim()
        val cedula = binding.etCedula.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val direccion = binding.etDireccion.text.toString().trim()

        if (nombre.isEmpty()) {
            binding.etNombre.error = "Campo requerido"
            return
        }
        if (cedula.isEmpty()) {
            binding.etCedula.error = "Campo requerido"
            return
        }
        if (telefono.isEmpty()) {
            binding.etTelefono.error = "Campo requerido"
            return
        }
        if (direccion.isEmpty()) {
            binding.etDireccion.error = "Campo requerido"
            return
        }

        var referencia: ReferenciaRequest? = null
        if (binding.switchReferencia.isChecked) {
            val refNombre = binding.etRefNombre.text.toString().trim()
            val refCedula = binding.etRefCedula.text.toString().trim()
            val refTelefono = binding.etRefTelefono.text.toString().trim()
            if (refNombre.isEmpty() || refCedula.isEmpty() || refTelefono.isEmpty()) {
                Toast.makeText(requireContext(), "Complete los datos de la referencia", Toast.LENGTH_SHORT).show()
                return
            }
            referencia = ReferenciaRequest(
                cedula = refCedula.toLongOrNull() ?: 0L,
                nombre = refNombre,
                celular = refTelefono.toLongOrNull() ?: 0L
            )
        }

        val zonaPos = binding.spinnerZona.selectedItemPosition
        val zonaId = if (zonaPos > 0 && zonaPos <= zonas.size) zonas[zonaPos - 1].id else 0

        val request = NuevoClienteRequest(
            cedula = cedula.toLongOrNull() ?: 0L,
            nombre = nombre,
            direccion = direccion,
            zonaId = zonaId,
            referencia = referencia
        )

        viewModel.guardarCliente(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
