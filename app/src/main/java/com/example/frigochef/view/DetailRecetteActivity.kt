package com.example.frigochef.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

/**
 * Affiche le détail complet d'une recette : image, informations clés,
 * liste des ingrédients avec statut possédé/manquant, et instructions numérotées.
 * Reçoit recette_id, score et ingredients via Intent.
 * Naviguée depuis ResultatsActivity (avec score) ou AccueilActivity (sans score).
 */

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
        val recetteId     = intent.getLongExtra("recette_id", -1L)
        score             = intent.getIntExtra("score", -1)
        // true si navigation depuis AccueilActivity — les icônes possédé/manquant seront cachées
        val depuisAccueil = intent.getBooleanExtra("depuis_accueil", false)

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
                score >= 75 -> ContextCompat.getColor(this, R.color.score_green_text)
                score >= 50 -> ContextCompat.getColor(this, R.color.score_yellow_text)
                else        -> ContextCompat.getColor(this, R.color.score_red_text)
            })
        }

        if (recetteId == -1L) {
            afficherErreur("Recette introuvable.")
            return
        }

        presenter = DetailPresenter(this, RecetteRepository(this))
        // Si navigation depuis Accueil, on passe une liste vide pour cacher les icônes
        presenter.chargerDetail(recetteId, if (depuisAccueil) emptyList() else ingredientsDispos)
    }

    // DetailContract.View

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
                score >= 75 -> ContextCompat.getColor(this, R.color.progress_green)
                score >= 50 -> ContextCompat.getColor(this, R.color.progress_yellow)
                else        -> ContextCompat.getColor(this, R.color.progress_red)
            })
            binding.progressBarCompatibilite.progressTintList =
                ColorStateList.valueOf(when {
                    score >= 75 -> ContextCompat.getColor(this, R.color.progress_green)
                    score >= 50 -> ContextCompat.getColor(this, R.color.progress_yellow)
                    else        -> ContextCompat.getColor(this, R.color.progress_red)
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

    // Code généré à l'aide de Claude
    // Cette fonction sert de solution de secours (fallback) lorsque la recette n'a pas d'image valide.
    // Elle assigne une couleur de fond et une icône thématique en se basant sur le type de cuisine de la recette.
    private fun afficherCouleurCuisine(typeCuisine: String) {
        val couleur = when (typeCuisine) {
            "Africaine"       -> ContextCompat.getColor(this, R.color.teal_50)
            "Indienne"        -> ContextCompat.getColor(this, R.color.cuisine_amber_bg)
            "Italienne"       -> ContextCompat.getColor(this, R.color.cuisine_purple_bg)
            "Mexicaine"       -> ContextCompat.getColor(this, R.color.cuisine_amber_bg)
            "Japonaise"       -> ContextCompat.getColor(this, R.color.cuisine_pink_bg)
            "Grecque"         -> ContextCompat.getColor(this, R.color.cuisine_blue_bg)
            "Américaine"      -> ContextCompat.getColor(this, R.color.cuisine_coral_bg)
            "Méditerranéenne" -> ContextCompat.getColor(this, R.color.score_green_bg)
            "Moyen-Orientale" -> ContextCompat.getColor(this, R.color.cuisine_amber_bg)
            "Québécoise"      -> ContextCompat.getColor(this, R.color.cuisine_purple_bg)
            else              -> ContextCompat.getColor(this, R.color.background_secondary)
        }
        binding.ivRecetteHero.setBackgroundColor(couleur)
        binding.ivRecetteHero.setImageResource(R.drawable.ic_kitchen)
        binding.ivRecetteHero.scaleType = android.widget.ImageView.ScaleType.CENTER
    }
}