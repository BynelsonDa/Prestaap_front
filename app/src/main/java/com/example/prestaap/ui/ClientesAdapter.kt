package com.example.prestaap.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.prestaap.R
import com.example.prestaap.data.model.Cliente
import com.example.prestaap.databinding.ItemClienteBinding

class ClientesAdapter(
    private val onClienteClick: (Cliente) -> Unit
) : RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder>() {

    private var clientes: List<Cliente> = emptyList()

    fun submitList(nuevaLista: List<Cliente>) {
        val diff = DiffUtil.calculateDiff(ClienteDiff(clientes, nuevaLista))
        clientes = nuevaLista
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ClienteViewHolder(ItemClienteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) =
        holder.bind(clientes[position])

    override fun getItemCount() = clientes.size

    inner class ClienteViewHolder(private val binding: ItemClienteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cliente: Cliente) {
            binding.tvNombre.text = cliente.nombre
            binding.tvZona.text = cliente.zona
            binding.tvCreditos.text = cliente.creditos.toString()
            binding.tvSaldoPrestado.text = formatPeso(cliente.saldoPrestado)
            binding.tvSaldoPendiente.text = formatPeso(cliente.saldoPendiente)
            applyBadge(cliente.estado)
            binding.root.setOnClickListener { onClienteClick(cliente) }
            binding.ivDetalle.setOnClickListener { onClienteClick(cliente) }
        }

        private fun applyBadge(estado: String) {
            val (bgRes, colorHex) = when (estado) {
                "Pendiente" -> Pair(R.drawable.bg_badge_pendiente, "#856404")
                "Pagado"    -> Pair(R.drawable.bg_badge_pagado,    "#155724")
                "Atrasado"  -> Pair(R.drawable.bg_badge_atrasado,  "#721C24")
                else        -> Pair(R.drawable.bg_badge_pendiente, "#856404")
            }
            binding.tvEstado.setBackgroundResource(bgRes)
            binding.tvEstado.setTextColor(Color.parseColor(colorHex))
            binding.tvEstado.text = estado
        }
    }

    private class ClienteDiff(
        private val old: List<Cliente>,
        private val new: List<Cliente>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size
        override fun getNewListSize() = new.size
        override fun areItemsTheSame(o: Int, n: Int) = old[o].id == new[n].id
        override fun areContentsTheSame(o: Int, n: Int) = old[o] == new[n]
    }
}

fun formatPeso(amount: Long): String =
    "$" + String.format("%,d", amount).replace(',', '.')
