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

/**
 * Adapter RecyclerView pour afficher les cartes de recettes en grille.
 * Supporte l'affichage avec ou sans badge de score de compatibilité.
 * La couleur de fond de l'image varie selon le type de cuisine.
 */

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

    /**
     * Appelé par RecyclerView pour remplir une carte avec les données
     * de la recette à la position donnée.
     */
    override fun onBindViewHolder(holder: RecetteViewHolder, position: Int) {
        holder.bind(recettes[position])
    }

    inner class RecetteViewHolder(
        private val binding: ItemRecetteCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recette: Recette) {
            val context = binding.root.context

            binding.tvNomRecette.text  = recette.nom
            binding.tvTemps.text       = "${recette.tempsPrep} min"
            binding.tvDifficulte.text  = recette.difficulte
            binding.tvTypeCuisine.text = recette.typeCuisine

            // Couleur de fond par type de cuisine
            val couleur = when (recette.typeCuisine) {
                "Africaine"       -> ContextCompat.getColor(context, R.color.teal_50)
                "Indienne"        -> ContextCompat.getColor(context, R.color.cuisine_amber_bg)
                "Italienne"       -> ContextCompat.getColor(context, R.color.cuisine_purple_bg)
                "Mexicaine"       -> ContextCompat.getColor(context, R.color.cuisine_amber_bg)
                "Japonaise"       -> ContextCompat.getColor(context, R.color.cuisine_pink_bg)
                "Grecque"         -> ContextCompat.getColor(context, R.color.cuisine_blue_bg)
                "Américaine"      -> ContextCompat.getColor(context, R.color.cuisine_coral_bg)
                "Méditerranéenne" -> ContextCompat.getColor(context, R.color.score_green_bg)
                "Moyen-Orientale" -> ContextCompat.getColor(context, R.color.cuisine_amber_bg)
                "Québécoise"      -> ContextCompat.getColor(context, R.color.cuisine_purple_bg)
                else              -> ContextCompat.getColor(context, R.color.background_secondary)
            }
            binding.ivRecette.setBackgroundColor(couleur)

            // Cherche l'image par son nom dans res/drawable-nodpi via getIdentifier().
            // Si l'image n'existe pas (resId == 0), affiche une icône placeholder.
            if (recette.imageUrl != null) {
                val resId = context.resources.getIdentifier(
                    recette.imageUrl, "drawable", context.packageName
                )
                if (resId != 0) {
                    binding.ivRecette.setImageResource(resId)
                    binding.ivRecette.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                } else {
                    binding.ivRecette.setImageResource(R.drawable.ic_kitchen)
                    binding.ivRecette.scaleType = android.widget.ImageView.ScaleType.CENTER
                }
            } else {
                binding.ivRecette.setImageResource(R.drawable.ic_kitchen)
                binding.ivRecette.scaleType = android.widget.ImageView.ScaleType.CENTER
            }

            // Badge score — affiché seulement si un score est disponible
            // (depuis ResultatsActivity). Caché depuis AccueilActivity.
            val score = scores[recette.id]
            if (score != null) {
                binding.tvScore.visibility = View.VISIBLE
                binding.tvScore.text       = "● $score%"
                binding.tvScore.setTextColor(when {
                    score >= 75 -> ContextCompat.getColor(context, R.color.score_green_text)
                    score >= 50 -> ContextCompat.getColor(context, R.color.score_yellow_text)
                    else        -> ContextCompat.getColor(context, R.color.score_red_text)
                })
            } else {
                binding.tvScore.visibility = View.GONE
            }

            // Chips diète et portions — maximum 2 chips affichées par carte
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
                    chipMinHeight       = 60f
                    chipCornerRadius    = 30f
                    chipStartPadding    = 4f
                    chipEndPadding      = 4f
                    iconStartPadding = 10f
                    iconEndPadding = 1f
                    textStartPadding = 10f
                    textEndPadding = 0f
                    chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.teal_50)
                    )
                    setTextColor(ContextCompat.getColor(context, R.color.teal_800))

                    // Icône ustensil seulement pour les portions
                    if (label.contains("portions")) {
                        chipIcon        = ContextCompat.getDrawable(context, R.drawable.ic_utensils)
                        isChipIconVisible = true
                        chipIconSize    = 32f
                        chipIconTint = ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.teal_800)
                        )
                    }
                }
                binding.chipGroupDiete.addView(chip)
            }

            binding.root.setOnClickListener { onClic(recette) }
        }
    }
}
