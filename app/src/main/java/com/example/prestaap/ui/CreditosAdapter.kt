package com.example.prestaap.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.prestaap.data.model.Credito
import com.example.prestaap.databinding.ItemCreditoBinding

class CreditosAdapter(
    private val onAbonarClick: (Credito) -> Unit,
    private val onCreditoClick: (Credito) -> Unit
) : RecyclerView.Adapter<CreditosAdapter.CreditoViewHolder>() {

    private var creditos: List<Credito> = emptyList()

    fun submitList(nuevaLista: List<Credito>) {
        val diff = DiffUtil.calculateDiff(CreditoDiff(creditos, nuevaLista))
        creditos = nuevaLista
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CreditoViewHolder(ItemCreditoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: CreditoViewHolder, position: Int) =
        holder.bind(creditos[position])

    override fun getItemCount() = creditos.size

    inner class CreditoViewHolder(private val binding: ItemCreditoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(credito: Credito) {
            binding.tvNombreCredito.text = credito.nombre
            binding.tvPrestado.text = formatPeso(credito.prestado)
            binding.tvRestante.text = formatPeso(credito.restante)
            binding.tvCuotas.text = "${credito.cuotasPagadas}/${credito.totalCuotas}"
            applyBadge(credito.estado)
            binding.btnAbonar.setOnClickListener { onAbonarClick(credito) }
            binding.root.setOnClickListener { onCreditoClick(credito) }
        }

        private fun applyBadge(estado: String) {
            val (bgColor, textColor) = when (estado) {
                "Pendiente" -> Pair("#FFF3CD", "#856404")
                "Atrasado"  -> Pair("#FFEBEE", "#C62828")
                "Pagado"    -> Pair("#E8F5E9", "#2E7D32")
                else        -> Pair("#FFF3CD", "#856404")
            }
            val density = binding.root.context.resources.displayMetrics.density
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor(bgColor))
                cornerRadius = 12 * density
            }
            binding.tvEstado.background = drawable
            binding.tvEstado.setTextColor(Color.parseColor(textColor))
            binding.tvEstado.text = estado
        }
    }

    private class CreditoDiff(
        private val old: List<Credito>,
        private val new: List<Credito>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size
        override fun getNewListSize() = new.size
        override fun areItemsTheSame(o: Int, n: Int) = old[o].id == new[n].id
        override fun areContentsTheSame(o: Int, n: Int) = old[o] == new[n]
    }
}
