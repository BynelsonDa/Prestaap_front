package com.example.prestaap.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.prestaap.data.model.MetodoPagoResumen
import com.example.prestaap.databinding.ItemMetodoPagoBinding

class MetodosPagoAdapter : RecyclerView.Adapter<MetodosPagoAdapter.MetodoViewHolder>() {

    private var metodos: List<MetodoPagoResumen> = emptyList()

    fun submitList(nuevaLista: List<MetodoPagoResumen>) {
        val diff = DiffUtil.calculateDiff(MetodoDiff(metodos, nuevaLista))
        metodos = nuevaLista
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetodoViewHolder {
        val binding = ItemMetodoPagoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MetodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MetodoViewHolder, position: Int) {
        holder.bind(metodos[position])
    }

    override fun getItemCount(): Int = metodos.size

    inner class MetodoViewHolder(private val binding: ItemMetodoPagoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(metodo: MetodoPagoResumen) {
            binding.tvMetodoNombre.text = metodo.metodoPago
            binding.tvMetodoMonto.text = formatPeso(metodo.monto.toLong())
            binding.tvMetodoInitial.text = if (metodo.metodoPago.isNotEmpty()) metodo.metodoPago.take(1).uppercase() else ""
        }
    }

    private class MetodoDiff(
        private val old: List<MetodoPagoResumen>,
        private val new: List<MetodoPagoResumen>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size
        override fun getNewListSize(): Int = new.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition].metodoPago == new[newItemPosition].metodoPago
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }
    }
}
