package com.example.prestaap.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prestaap.data.model.Zona
import com.example.prestaap.databinding.ItemZonaAddBinding
import com.example.prestaap.databinding.ItemZonaBinding

class ZonasAdapter(
    private var zonas: List<Zona>,
    private val onZonaClick: (Zona) -> Unit,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ADD = 0
        private const val TYPE_ZONA = 1
    }

    override fun getItemViewType(position: Int) = if (position == 0) TYPE_ADD else TYPE_ZONA

    override fun getItemCount() = zonas.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ADD) {
            AddViewHolder(ItemZonaAddBinding.inflate(inflater, parent, false))
        } else {
            ZonaViewHolder(ItemZonaBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddViewHolder  -> holder.bind(onAddClick)
            is ZonaViewHolder -> holder.bind(zonas[position - 1])
        }
    }

    fun updateZonas(nuevasZonas: List<Zona>) {
        zonas = nuevasZonas
        notifyDataSetChanged()
    }

    inner class AddViewHolder(private val binding: ItemZonaAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(onClick: () -> Unit) { binding.root.setOnClickListener { onClick() } }
    }

    inner class ZonaViewHolder(private val binding: ItemZonaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(zona: Zona) {
            binding.tvZonaNombre.text = zona.nombre
            binding.root.setOnClickListener { onZonaClick(zona) }
        }
    }
}
