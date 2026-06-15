package com.example.prestaap.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.prestaap.data.model.PagoHistorialResponse
import com.example.prestaap.databinding.ItemHistorialPagoBinding

class HistorialPagosAdapter(
    private val onDetalleClick: (PagoHistorialResponse) -> Unit
) : RecyclerView.Adapter<HistorialPagosAdapter.PagoViewHolder>() {

    private var pagos: List<PagoHistorialResponse> = emptyList()

    fun submitList(nuevaLista: List<PagoHistorialResponse>) {
        val diff = DiffUtil.calculateDiff(PagoDiff(pagos, nuevaLista))
        pagos = nuevaLista
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagoViewHolder {
        val binding = ItemHistorialPagoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PagoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagoViewHolder, position: Int) {
        holder.bind(pagos[position])
    }

    override fun getItemCount(): Int = pagos.size

    inner class PagoViewHolder(private val binding: ItemHistorialPagoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pago: PagoHistorialResponse) {
            binding.tvMontoTotal.text = formatPeso(pago.montoTotal.toLong())
            binding.tvMetodoPago.text = pago.metodoPago
            
            // Reemplazar la T de la fecha por un espacio
            val fechaFormateada = pago.fechaPago.replace("T", " ")
            binding.tvFecha.text = fechaFormateada

            binding.btnDetalle.setOnClickListener {
                onDetalleClick(pago)
            }
        }
    }

    private class PagoDiff(
        private val old: List<PagoHistorialResponse>,
        private val new: List<PagoHistorialResponse>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size
        override fun getNewListSize(): Int = new.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition].idPago == new[newItemPosition].idPago
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }
    }
}
