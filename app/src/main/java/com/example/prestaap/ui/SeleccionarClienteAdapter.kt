package com.example.prestaap.ui

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.prestaap.data.model.ClienteResponse
import com.example.prestaap.databinding.ItemSeleccionarClienteBinding

class SeleccionarClienteAdapter(
    private var clientes: List<ClienteResponse>,
    private val onClienteClick: (ClienteResponse) -> Unit
) : RecyclerView.Adapter<SeleccionarClienteAdapter.ViewHolder>() {

    private val avatarColors = listOf("#E3F2FD", "#FCE4EC", "#E8F5E9", "#FFF3E0")

    inner class ViewHolder(val binding: ItemSeleccionarClienteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSeleccionarClienteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cliente = clientes[position]
        with(holder.binding) {
            tvNombre.text = cliente.nombre
            tvDireccion.text = cliente.direccion
            tvAvatar.text = cliente.nombre.firstOrNull()?.uppercase() ?: "?"

            val color = android.graphics.Color.parseColor(avatarColors[position % avatarColors.size])
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
            }
            ivAvatar.background = drawable

            root.setOnClickListener { onClienteClick(cliente) }
        }
    }

    override fun getItemCount() = clientes.size

    fun updateClientes(nuevos: List<ClienteResponse>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = clientes.size
            override fun getNewListSize() = nuevos.size
            override fun areItemsTheSame(o: Int, n: Int) = clientes[o].cedula == nuevos[n].cedula
            override fun areContentsTheSame(o: Int, n: Int) = clientes[o] == nuevos[n]
        })
        clientes = nuevos
        diff.dispatchUpdatesTo(this)
    }
}
