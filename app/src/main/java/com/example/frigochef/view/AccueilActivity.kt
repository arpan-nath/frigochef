package com.example.frigochef.view

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frigochef.R
import com.example.frigochef.contract.AccueilContract
import com.example.frigochef.databinding.ActivityAccueilBinding
import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.model.repository.RecetteRepository
import com.example.frigochef.model.repository.SessionRepository
import com.example.frigochef.presenter.AccueilPresentateur
import com.example.frigochef.view.adapter.RecetteAdapter
import com.google.android.material.chip.Chip
import com.example.frigochef.model.entity.FiltreRecette

class AccueilActivity : AppCompatActivity(), AccueilContract.View {

    private lateinit var binding: ActivityAccueilBinding
    private lateinit var presentateur: AccueilPresentateur
    private lateinit var adapter: RecetteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccueilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ── 1. Adapter + RecyclerView en grille 2 colonnes ──
        adapter = RecetteAdapter { recette -> naviguerDetail(recette.id) }
        binding.rvRecettes.adapter = adapter
        binding.rvRecettes.layoutManager = GridLayoutManager(this, 2)

        // ── 2. Présentateur ──
        presentateur = AccueilPresentateur(this, RecetteRepository(this))

        // ── 3. Recherche en temps réel ──
        binding.etRecherche.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                presentateur.rechercherRecettes(s.toString())
            }
        })

        // ── 4. Bouton Inspire-moi ──
        binding.btnInspireMe.setOnClickListener {
            startActivity(Intent(this, QuestionnaireActivity::class.java))
        }

        // ── 5. Chips de filtres rapides ──
        binding.chipTout.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) presentateur.chargerRecettes()
        }
        binding.chipFacile.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) presentateur.filtrerParDifficulte("Facile")
            else presentateur.chargerRecettes()
        }
        binding.chipVege.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) presentateur.filtrerParDiete(isVege = true)
            else presentateur.chargerRecettes()
        }
        binding.chipRapide.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) presentateur.filtrerParTemps(30)
            else presentateur.chargerRecettes()
        }

        binding.btnFiltres.setOnClickListener {
            val panneau = PanneauFiltresFragment().apply {
                filtresActuels = FiltreRecette()
                ingredientsDispos = emptyList()
                onFiltresAppliques = { nouveauxFiltres ->
                    presentateur.filtrerParFiltres(nouveauxFiltres)
                }
            }
            panneau.show(supportFragmentManager, "filtres")
        }

        // ── 6. Charger toutes les recettes au démarrage ──
        presentateur.chargerRecettes()

        // ── 7. Chips session dans le hero ──
        afficherChipsSession()
    }

    private fun afficherChipsSession() {
        val sessionRepo = SessionRepository(this)
        val ingredientRepo = IngredientRepository(this)
        val ids = sessionRepo.findAllIds()

        binding.chipGroupSessionIngredients.removeAllViews()
        if (ids.isEmpty()) return

        ids.take(3).forEach { id ->
            val ingredient = ingredientRepo.findById(id) ?: return@forEach
            val chip = Chip(this).apply {
                text = "✓ ${ingredient.nom}"
                textSize = 10f
                isClickable = false
                isCheckable = false
                chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(this@AccueilActivity, R.color.teal_100)
                )
                setTextColor(ContextCompat.getColor(this@AccueilActivity, R.color.teal_900))
                chipMinHeight = 28f
            }
            binding.chipGroupSessionIngredients.addView(chip)
        }

        if (ids.size > 3) {
            val chip = Chip(this).apply {
                text = "+ ${ids.size - 3} autres..."
                textSize = 10f
                isClickable = false
                isCheckable = false
                chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(this@AccueilActivity, R.color.background_secondary)
                )
                setTextColor(ContextCompat.getColor(this@AccueilActivity, R.color.text_secondary))
                chipMinHeight = 28f
            }
            binding.chipGroupSessionIngredients.addView(chip)
        }
    }

    override fun afficherRecettes(recettes: List<Recette>) {
        binding.layoutVide.visibility = View.GONE
        binding.rvRecettes.visibility = View.VISIBLE
        adapter.soumettre(recettes)
        binding.tvCompteur.text = "${recettes.size} recette(s) dans le catalogue"
    }

    override fun afficherMessageVide() {
        binding.rvRecettes.visibility = View.GONE
        binding.layoutVide.visibility = View.VISIBLE
        binding.tvCompteur.text = "0 recette trouvée"
    }

    override fun afficherErreur(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun naviguerDetail(id: Long) {
        val intent = Intent(this, DetailRecetteActivity::class.java)
        intent.putExtra("recette_id", id)
        startActivity(intent)
    }
}