package com.example.frigochef.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.frigochef.databinding.ItemEtapeInstructionBinding

/**
 * Adapter RecyclerView pour afficher les étapes d'une recette.
 * Parse le texte des instructions en séparant par saut de ligne,
 * supprime les numéros existants via regex, puis rénuméote automatiquement.
 */

class InstructionAdapter : RecyclerView.Adapter<InstructionAdapter.VH>() {

    private var etapes = listOf<String>()

    fun soumettre(instructions: String) {
        etapes = instructions
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            // Supprime les numéros existants (ex: "1. ", "2) ", "3- ") pour rénuméroter automatiquement dans onBindViewHolder
            .map { it.replace(Regex("^\\d+[.)\\-]\\s*"), "").trim() }
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

    /**
     * Appelé par RecyclerView pour remplir une carte avec les données
     * de l'étape à la position donnée.
     * holder.binding donne accès aux vues de la carte —
     * position + 1 pour afficher 1, 2, 3... au lieu de 0, 1, 2...
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.tvNumeroEtape.text = "${position + 1}"
        holder.binding.tvTextEtape.text   = etapes[position]
    }
}