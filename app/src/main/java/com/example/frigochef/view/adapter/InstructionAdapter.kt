package com.example.frigochef.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.frigochef.databinding.ItemEtapeInstructionBinding

class InstructionAdapter : RecyclerView.Adapter<InstructionAdapter.VH>() {

    private var etapes = listOf<String>()

    fun soumettre(instructions: String) {
        etapes = instructions
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapIndexed { index, texte ->
                texte.removePrefix("${index + 1}. ").trim()
            }
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemEtapeInstructionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemEtapeInstructionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun getItemCount() = etapes.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.tvNumeroEtape.text = "${position + 1}"
        holder.binding.tvTextEtape.text   = etapes[position]
    }
}