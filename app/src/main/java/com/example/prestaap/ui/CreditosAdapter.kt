package com.example.prestaap.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.prestaap.data.model.CreditoResumen
import com.example.prestaap.databinding.ItemCreditoBinding

class CreditosAdapter(
    private val onAbonarClick: (CreditoResumen, Int) -> Unit,
    private val onCreditoClick: (CreditoResumen, Int) -> Unit
) : RecyclerView.Adapter<CreditosAdapter.CreditoViewHolder>() {

    private var creditos: List<CreditoResumen> = emptyList()

    fun submitList(nuevaLista: List<CreditoResumen>) {
        val diff = DiffUtil.calculateDiff(CreditoDiff(creditos, nuevaLista))
        creditos = nuevaLista
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CreditoViewHolder(ItemCreditoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: CreditoViewHolder, position: Int) =
        holder.bind(creditos[position], position)

    override fun getItemCount() = creditos.size

    inner class CreditoViewHolder(private val binding: ItemCreditoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(credito: CreditoResumen, position: Int) {
            binding.tvNombreCredito.text = credito.label
            binding.tvPrestado.text = formatPeso(credito.montoPrestamo.toLong())
            binding.tvRestante.text = formatPeso(credito.montoRestante.toLong())
            binding.tvCuotas.text = "${credito.cuotasPagadas}/${credito.totalCuotas}"
            applyBadge(credito.estado)
            binding.btnAbonar.setOnClickListener { onAbonarClick(credito, position) }
            binding.root.setOnClickListener { onCreditoClick(credito, position) }
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
        private val old: List<CreditoResumen>,
        private val new: List<CreditoResumen>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size
        override fun getNewListSize() = new.size
        override fun areItemsTheSame(o: Int, n: Int) = old[o].label == new[n].label
        override fun areContentsTheSame(o: Int, n: Int) = old[o] == new[n]
    }
}
