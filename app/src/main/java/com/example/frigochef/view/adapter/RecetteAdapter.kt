package com.example.frigochef.view.adapter



import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frigochef.R
import com.example.frigochef.databinding.ItemRecetteCardBinding
import com.example.frigochef.model.entity.Recette
import com.google.android.material.chip.Chip

class RecetteAdapter(
    private val onClic: (Recette) -> Unit
) : RecyclerView.Adapter<RecetteAdapter.RecetteViewHolder>() {

    private var recettes: List<Recette> = emptyList()
    private var scores: Map<Long, Int> = emptyMap()

    // ── Met à jour la liste et rafraîchit le RecyclerView ──
    fun soumettre(nouvelles: List<Recette>, nouveauxScores: Map<Long, Int> = emptyMap()) {
        recettes = nouvelles
        scores = nouveauxScores
        notifyDataSetChanged()
    }

    override fun getItemCount() = recettes.size

    // ── 1. Gonfle le XML de la carte ──
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetteViewHolder {
        val binding = ItemRecetteCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecetteViewHolder(binding)
    }

    // ── 2. Remplit la carte avec les données ──
    override fun onBindViewHolder(holder: RecetteViewHolder, position: Int) {
        holder.bind(recettes[position])
    }

    inner class RecetteViewHolder(
        private val binding: ItemRecetteCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recette: Recette) {

            // Nom, temps, difficulté, cuisine
            binding.tvNomRecette.text = recette.nom
            binding.tvTemps.text = "${recette.tempsPrep} min"
            binding.tvDifficulte.text = recette.difficulte
            binding.tvTypeCuisine.text = recette.typeCuisine

            // Image — placeholder pour l'instant
            binding.ivRecette.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, R.color.teal_50)
            )
            binding.ivRecette.setImageResource(R.drawable.ic_kitchen)
            binding.ivRecette.scaleType = android.widget.ImageView.ScaleType.CENTER

            // Badge score — caché dans AccueilActivity
            val score = scores[recette.id]
            if (score != null) {
                binding.tvScore.visibility = View.VISIBLE
                binding.tvScore.text = "$score%"
                binding.tvScore.setTextColor(
                    ContextCompat.getColor(binding.root.context, when {
                        score >= 75 -> R.color.score_green_text
                        score >= 50 -> R.color.score_yellow_text
                        else        -> R.color.score_red_text
                    })
                )
            } else {
                binding.tvScore.visibility = View.GONE
            }

            // Pills diète + portions
            binding.chipGroupDiete.removeAllViews()
            val context = binding.root.context
            val dietes = buildList {
                if (recette.isVege)       add("🌿 Végé")
                if (recette.isVegan)      add("🌱 Végane")
                if (recette.isSansGluten) add("🌾 Sans gluten")
                if (recette.portions > 0) add("👤 ${recette.portions} portions")
            }
            dietes.forEach { label ->
                val chip = Chip(context).apply {
                    text = label
                    textSize = 9f
                    isClickable = false
                    isCheckable = false
                    chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.teal_50)
                    )
                    setTextColor(ContextCompat.getColor(context, R.color.teal_800))
                    chipMinHeight = 24f
                }
                binding.chipGroupDiete.addView(chip)
            }

            // Clic sur la carte
            binding.root.setOnClickListener { onClic(recette) }
        }
    }
}