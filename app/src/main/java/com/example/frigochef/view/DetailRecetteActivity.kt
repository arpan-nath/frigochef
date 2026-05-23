package com.example.frigochef.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.example.frigochef.R
import com.example.frigochef.contract.DetailContract
import com.example.frigochef.databinding.ActivityDetailsRecetteBinding
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.entity.RecetteIngredientDetail
import com.example.frigochef.model.repository.RecetteRepository
import com.example.frigochef.presenter.DetailPresenter
import com.example.frigochef.view.adapter.IngredientDetailAdapter
import com.example.frigochef.view.adapter.InstructionAdapter

class DetailRecetteActivity : AppCompatActivity(), DetailContract.View {

    private lateinit var binding:            ActivityDetailsRecetteBinding
    private lateinit var presenter:          DetailPresenter
    private lateinit var ingredientAdapter:  IngredientDetailAdapter
    private lateinit var instructionAdapter: InstructionAdapter

    private var ingredientsDispos      = listOf<IngredientQuantite>()
    private var score                  = -1
    private var instructionsCourantes  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsRecetteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Données depuis l'Intent
        val recetteId = intent.getLongExtra("recette_id", -1L)
        score         = intent.getIntExtra("score", -1)

        @Suppress("UNCHECKED_CAST")
        ingredientsDispos = (intent.getSerializableExtra("ingredients") as? ArrayList<IngredientQuantite>)
            ?.toList() ?: emptyList()

        // Toolbar
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Adapters
        ingredientAdapter  = IngredientDetailAdapter()
        instructionAdapter = InstructionAdapter()

        binding.rvIngredients.layoutManager  = LinearLayoutManager(this)
        binding.rvIngredients.adapter        = ingredientAdapter
        binding.rvInstructions.layoutManager = LinearLayoutManager(this)
        binding.rvInstructions.adapter       = instructionAdapter

        // Badge score
        if (score >= 0) {
            binding.tvScoreHero.isVisible = true
            binding.tvScoreHero.text      = "$score% compatible"
            binding.tvScoreHero.setTextColor(when {
                score >= 75 -> Color.parseColor("#2E7D32")
                score >= 50 -> Color.parseColor("#F57F17")
                else        -> Color.parseColor("#C62828")
            })
        }

        if (recetteId == -1L) {
            afficherErreur("Recette introuvable.")
            return
        }

        presenter = DetailPresenter(this, RecetteRepository(this))
        presenter.chargerDetail(recetteId, ingredientsDispos)
    }

    // ── DetailContract.View ───────────────────────────────────────────────────

    override fun afficherRecette(recette: Recette) {
        binding.tvNomRecette.text  = recette.nom
        binding.tvDescription.text = recette.description ?: ""
        binding.tvTempsPrep.text   = "${recette.tempsPrep} min"
        binding.tvDifficulte.text  = recette.difficulte
        binding.tvTypeRepas.text   = recette.typeRepas

        // Sauvegarder les instructions pour afficherIngredients()
        instructionsCourantes = recette.instructions

        // Image
        if (recette.imageUrl != null) {
            val resId = resources.getIdentifier(recette.imageUrl, "drawable", packageName)
            if (resId != 0) {
                binding.ivRecetteHero.setImageResource(resId)
                binding.ivRecetteHero.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            } else {
                afficherCouleurCuisine(recette.typeCuisine)
            }
        } else {
            afficherCouleurCuisine(recette.typeCuisine)
        }

        // Chips
        binding.chipGroupPills.removeAllViews()
        val labels = buildList {
            add(recette.typeCuisine)
            if (recette.isVege)       add("🌿 Végé")
            if (recette.isVegan)      add("🌱 Végane")
            if (recette.isSansGluten) add("🌾 Sans gluten")
            if (recette.portions > 0) add("${recette.portions} portions")
        }
        labels.forEach { label ->
            val chip = Chip(this).apply {
                text                 = label
                isCheckable          = false
                isCheckedIconVisible = false
                textSize             = 10f
                chipMinHeight        = 28f
            }
            binding.chipGroupPills.addView(chip)
        }

        // Barre de compatibilité
        if (score >= 0) {
            binding.layoutCompatibilite.isVisible     = true
            binding.progressBarCompatibilite.progress = score
            binding.tvPourcentageCompatibilite.text   = "$score%"
            binding.tvPourcentageCompatibilite.setTextColor(when {
                score >= 75 -> Color.parseColor("#27500A")
                score >= 50 -> Color.parseColor("#854F0B")
                else        -> Color.parseColor("#791F1F")
            })
            binding.progressBarCompatibilite.progressTintList =
                ColorStateList.valueOf(when {
                    score >= 75 -> Color.parseColor("#639922")
                    score >= 50 -> Color.parseColor("#EF9F27")
                    else        -> Color.parseColor("#E24B4A")
                })
        }
    }

    override fun afficherIngredients(
        ingredients:       List<RecetteIngredientDetail>,
        ingredientsDispos: List<IngredientQuantite>
    ) {
        ingredientAdapter.soumettre(ingredients, ingredientsDispos)
        instructionAdapter.soumettre(instructionsCourantes)
    }

    override fun afficherErreur(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
        finish()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun afficherCouleurCuisine(typeCuisine: String) {
        val couleur = when (typeCuisine) {
            "Africaine"       -> Color.parseColor("#E1F5EE")
            "Indienne"        -> Color.parseColor("#FFF3E0")
            "Italienne"       -> Color.parseColor("#F3E5F5")
            "Mexicaine"       -> Color.parseColor("#FFF9C4")
            "Japonaise"       -> Color.parseColor("#FCE4EC")
            "Grecque"         -> Color.parseColor("#E3F2FD")
            "Américaine"      -> Color.parseColor("#FBE9E7")
            "Méditerranéenne" -> Color.parseColor("#E8F5E9")
            "Moyen-Orientale" -> Color.parseColor("#FFF8E1")
            "Québécoise"      -> Color.parseColor("#E8EAF6")
            else              -> Color.parseColor("#F5F4F0")
        }
        binding.ivRecetteHero.setBackgroundColor(couleur)
        binding.ivRecetteHero.setImageResource(R.drawable.ic_kitchen)
        binding.ivRecetteHero.scaleType = android.widget.ImageView.ScaleType.CENTER
    }
}