package com.example.prestaap.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prestaap.data.model.AbonoResponse
import com.example.prestaap.databinding.ItemReciboCuotaBinding

/** Muestra la distribución del pago: una fila por cuota con Capital, Interés y Estado. */
class ReciboCuotaAdapter(
    private val abonos: List<AbonoResponse>
) : RecyclerView.Adapter<ReciboCuotaAdapter.CuotaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CuotaViewHolder(ItemReciboCuotaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: CuotaViewHolder, position: Int) =
        holder.bind(abonos[position])

    override fun getItemCount() = abonos.size

    inner class CuotaViewHolder(private val binding: ItemReciboCuotaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(abono: AbonoResponse) {
            val numero = abono.cuota?.numeroDeCuota
            binding.tvCuotaNumero.text = if (numero != null) "Cuota $numero" else "Cuota"
            binding.tvCapital.text = formatPeso(abono.capitalAbonado.toLong())
            binding.tvInteres.text = formatPeso(abono.interesAbonado.toLong())
            binding.tvEstado.text = abono.cuota?.estado?.nombre
                ?.replaceFirstChar { it.uppercase() } ?: "-"
        }
    }
}
