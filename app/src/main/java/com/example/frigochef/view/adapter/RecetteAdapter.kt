package com.example.frigochef.view.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frigochef.R
import com.example.frigochef.databinding.ItemRecetteCardBinding
import com.example.frigochef.model.ScoreCalculateur
import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.entity.RecetteAvecScore
import com.google.android.material.chip.Chip

class RecetteAdapter(
    private val onClic: (Recette) -> Unit
) : RecyclerView.Adapter<RecetteAdapter.RecetteViewHolder>() {

    private var recettes: List<Recette>  = emptyList()
    private var scores:   Map<Long, Int> = emptyMap()

    fun soumettre(nouvelles: List<Recette>, nouveauxScores: Map<Long, Int> = emptyMap()) {
        recettes = nouvelles
        scores   = nouveauxScores
        notifyDataSetChanged()
    }

    override fun getItemCount() = recettes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetteViewHolder {
        val binding = ItemRecetteCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecetteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecetteViewHolder, position: Int) {
        holder.bind(recettes[position])
    }

    inner class RecetteViewHolder(
        private val binding: ItemRecetteCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recette: Recette) {
            val context = binding.root.context

            // Nom, temps, difficulté, cuisine
            binding.tvNomRecette.text  = recette.nom
            binding.tvTemps.text       = "${recette.tempsPrep} min"
            binding.tvDifficulte.text  = recette.difficulte
            binding.tvTypeCuisine.text = recette.typeCuisine

            // Couleur de fond par type de cuisine
            val couleur = when (recette.typeCuisine) {
                "Africaine"        -> Color.parseColor("#E1F5EE")
                "Indienne"         -> Color.parseColor("#FFF3E0")
                "Italienne"        -> Color.parseColor("#F3E5F5")
                "Mexicaine"        -> Color.parseColor("#FFF9C4")
                "Japonaise"        -> Color.parseColor("#FCE4EC")
                "Grecque"          -> Color.parseColor("#E3F2FD")
                "Américaine"       -> Color.parseColor("#FBE9E7")
                "Méditerranéenne"  -> Color.parseColor("#E8F5E9")
                "Moyen-Orientale"  -> Color.parseColor("#FFF8E1")
                "Québécoise"       -> Color.parseColor("#E8EAF6")
                else               -> Color.parseColor("#F5F4F0")
            }
            binding.ivRecette.setBackgroundColor(couleur)
            binding.ivRecette.setImageResource(R.drawable.ic_kitchen)
            binding.ivRecette.scaleType = android.widget.ImageView.ScaleType.CENTER

            // Badge score
            val score = scores[recette.id]
            if (score != null) {
                binding.tvScore.visibility = View.VISIBLE
                binding.tvScore.text       = "● $score%"
                binding.tvScore.setTextColor(when {
                    score >= 75 -> Color.parseColor("#2E7D32")
                    score >= 50 -> Color.parseColor("#F57F17")
                    else        -> Color.parseColor("#C62828")
                })
            } else {
                binding.tvScore.visibility = View.GONE
            }

            // Pills diète + portions
            binding.chipGroupDiete.removeAllViews()
            val labels = buildList {
                if (recette.isVege)       add("🌿 Végé")
                if (recette.isVegan)      add("🌱 Végane")
                if (recette.isSansGluten) add("🌾 Sans gluten")
                if (recette.portions > 0) add("${recette.portions} portions")
            }

            labels.take(2).forEach { label ->
                val chip = Chip(context).apply {
                    text                = label
                    textSize            = 9f
                    isClickable         = false
                    isCheckable         = false
                    chipMinHeight       = 36f
                    chipCornerRadius    = 20f
                    chipStartPadding    = 4f
                    chipEndPadding      = 2f
                    iconStartPadding = 4f
                    iconEndPadding = 4f
                    textStartPadding = 0f
                    textEndPadding = 0f
                    chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#E1F5EE"))
                    setTextColor(Color.parseColor("#085041"))

                    // Icône ustensil seulement pour les portions
                    if (label.contains("portions")) {
                        chipIcon        = ContextCompat.getDrawable(context, R.drawable.ic_utensils)
                        isChipIconVisible = true
                        chipIconSize    = 32f
                        chipIconTint    = ColorStateList.valueOf(Color.parseColor("#085041"))
                    }
                }
                binding.chipGroupDiete.addView(chip)
            }

            // Clic
            binding.root.setOnClickListener { onClic(recette) }
        }
    }
}
