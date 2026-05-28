package com.example.frigochef.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frigochef.contract.ResultatsContract
import com.example.frigochef.databinding.ActivityResultatsBinding
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.entity.RecetteAvecScore
import com.example.frigochef.model.repository.RecetteRepository
import com.example.frigochef.presenter.ResultatsPresenter
import com.example.frigochef.view.adapter.RecetteAdapter
import android.content.Intent

/**
 * Affiche les recettes filtrées et triées par score de compatibilité décroissant.
 * Reçoit FiltreRecette et List<IngredientQuantite> via Intent depuis QuestionnaireActivity.
 * Supporte la recherche locale par nom et le raffinage des filtres via PanneauFiltresFragment.
 */

class ResultatsActivity : AppCompatActivity(), ResultatsContract.View {

    private lateinit var binding:         ActivityResultatsBinding
    private lateinit var presenter:       ResultatsPresenter
    private lateinit var adapter:         RecetteAdapter

    private var tousLesResultats  = listOf<RecetteAvecScore>()
    private var filtresActuels    = FiltreRecette()
    private var ingredientsDispos = listOf<IngredientQuantite>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filtresActuels = intent.getSerializableExtra("filtres") as? FiltreRecette
            ?: FiltreRecette()
        @Suppress("UNCHECKED_CAST")
        ingredientsDispos = intent.getSerializableExtra("ingredients")
                as? List<IngredientQuantite> ?: emptyList()

        adapter = RecetteAdapter { recette ->
            naviguerVersDetail(recette.id, ingredientsDispos)
        }
        binding.rvResultats.layoutManager = GridLayoutManager(this, 2)
        binding.rvResultats.adapter       = adapter

        presenter = ResultatsPresenter(this, RecetteRepository(this))
        presenter.chargerResultats(filtresActuels, ingredientsDispos)

        binding.btnRetour.setOnClickListener { finish() }

        binding.btnRefaireQuestionnaire.setOnClickListener { finish() }

        binding.btnModifierFiltres.setOnClickListener { finish() }

        binding.etRecherche.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filtrerLocalement(s.toString())
            }
        })

        afficherChipsFiltresActifs(filtresActuels)

        binding.btnOuvrirFiltres.setOnClickListener {
            val ingredientRepo = com.example.frigochef.model.repository.IngredientRepository(this)
            val noms = ingredientsDispos.associate { iq ->
                iq.ingredientId to (ingredientRepo.findById(iq.ingredientId)?.nom ?: "Ingrédient ${iq.ingredientId}")
            }

            val panneau = PanneauFiltresFragment().apply {
                filtresActuels    = this@ResultatsActivity.filtresActuels
                ingredientsDispos = this@ResultatsActivity.ingredientsDispos
                nomsIngredients   = noms
                onFiltresAppliques = { nouveauxFiltres ->
                    this@ResultatsActivity.filtresActuels = nouveauxFiltres
                    presenter.chargerResultats(nouveauxFiltres, ingredientsDispos)
                    binding.chipGroupFiltresActifs.removeAllViews()
                    afficherChipsFiltresActifs(nouveauxFiltres)
                }
            }
            panneau.show(supportFragmentManager, "filtres")
        }
    }

    override fun afficherResultats(recettes: List<RecetteAvecScore>) {
        tousLesResultats = recettes
        binding.rvResultats.isVisible = true
        binding.layoutVide.isVisible  = false
        val listeRecettes = recettes.map { it.recette }
        val mapScores     = recettes.associate { it.recette.id to it.score }
        adapter.soumettre(listeRecettes, mapScores)
    }

    override fun afficherEtatVide() {
        binding.rvResultats.isVisible = false
        binding.layoutVide.isVisible  = true
    }

    override fun afficherNombreResultats(count: Int) {
        binding.tvNombreResultats.text =
            "$count recette${if (count > 1) "s" else ""} correspondent à votre sélection"
    }

    private fun filtrerLocalement(query: String) {
        if (query.isEmpty()) {
            val listeRecettes = tousLesResultats.map { it.recette }
            val mapScores     = tousLesResultats.associate { it.recette.id to it.score }
            adapter.soumettre(listeRecettes, mapScores)
            return
        }
        val filtre = tousLesResultats.filter {
            it.recette.nom.contains(query, ignoreCase = true)
        }
        if (filtre.isEmpty()) afficherEtatVide()
        else {
            binding.rvResultats.isVisible = true
            binding.layoutVide.isVisible  = false
            adapter.soumettre(filtre.map { it.recette }, filtre.associate { it.recette.id to it.score })
        }
    }

    private fun afficherChipsFiltresActifs(filtres: FiltreRecette) {
        val chips = mutableListOf<String>()
        filtres.typeCuisine?.let  { chips.add(it) }
        filtres.typeRepas?.let    { chips.add(it) }
        filtres.difficulte?.let   { chips.add(it) }
        filtres.tempsMax?.let     { chips.add("≤ ${it} min") }
        if (filtres.isVege)       chips.add("Végé")
        if (filtres.isVegan)      chips.add("Végane")
        if (filtres.isSansGluten) chips.add("Sans gluten")

        chips.forEach { label ->
            val chip = com.google.android.material.chip.Chip(this).apply {
                text           = label
                isCheckable    = false
                isCloseIconVisible = false
                textSize       = 10f
            }
            binding.chipGroupFiltresActifs.addView(chip)
        }
    }





    private fun naviguerVersDetail(recetteId: Long, ingredientsDispos: List<IngredientQuantite>) {
        val intent = Intent(this, DetailRecetteActivity::class.java).apply {
            putExtra("recette_id", recetteId)
            putExtra("ingredients", ArrayList(ingredientsDispos))
            putExtra("score", tousLesResultats
                .find { it.recette.id == recetteId }?.score ?: 0)
        }
        startActivity(intent)
    }
}