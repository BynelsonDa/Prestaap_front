package com.example.prestaap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prestaap.databinding.BottomSheetAbonarBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AbonarBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAbonarBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(creditoId: Int?) = AbonarBottomSheet().apply {
            arguments = Bundle().apply {
                if (creditoId != null) putInt("creditoId", creditoId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAbonarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val creditoId = arguments?.let { if (it.containsKey("creditoId")) it.getInt("creditoId") else null }
        binding.tvAbonarTitle.text = if (creditoId != null) "Abonar crédito #$creditoId" else "Abonar a todos"
        binding.btnConfirmar.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
