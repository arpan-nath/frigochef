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
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.model.repository.RecetteRepository
import com.example.frigochef.model.repository.SessionRepository
import com.example.frigochef.presenter.AccueilPresenter
import com.example.frigochef.view.adapter.RecetteAdapter
import com.google.android.material.chip.Chip

class AccueilActivity : AppCompatActivity(), AccueilContract.View {

    private lateinit var binding:      ActivityAccueilBinding
    private lateinit var presentateur: AccueilPresenter
    private lateinit var adapter:      RecetteAdapter

    // État des filtres rapides — mémorisé pour permettre la combinaison de plusieurs filtres
    private var filtreDifficulte: String?  = null
    private var filtreIsVege:     Boolean  = false
    private var filtreTempsMax:   Int?     = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccueilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ── 1. Adapter + RecyclerView en grille 2 colonnes ──
        adapter = RecetteAdapter { recette -> naviguerDetail(recette.id) }
        binding.rvRecettes.adapter = adapter
        binding.rvRecettes.layoutManager = GridLayoutManager(this, 2)

        // ── 2. Présentateur ──
        presentateur = AccueilPresenter(
            this,
            RecetteRepository(this),
            SessionRepository(this),
            IngredientRepository(this)
        )

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
        // Chaque chip mémorise son état et appelle appliquerFiltresCombines()
        // pour construire un FiltreRecette combiné avant d'appeler le présentateur
        binding.chipTout.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                filtreDifficulte = null
                filtreIsVege     = false
                filtreTempsMax   = null
                presentateur.chargerRecettes()
            }
        }
        binding.chipFacile.setOnCheckedChangeListener { _, isChecked ->
            filtreDifficulte = if (isChecked) "Facile" else null
            appliquerFiltresCombines()
        }
        binding.chipVege.setOnCheckedChangeListener { _, isChecked ->
            filtreIsVege = isChecked
            appliquerFiltresCombines()
        }
        binding.chipRapide.setOnCheckedChangeListener { _, isChecked ->
            filtreTempsMax = if (isChecked) 30 else null
            appliquerFiltresCombines()
        }

        // ── 5b. Bouton filtre sidebar ──
        binding.btnFiltres.setOnClickListener {
            val panneau = PanneauFiltresFragment().apply {
                filtresActuels    = FiltreRecette()
                ingredientsDispos = emptyList()
                onFiltresAppliques = { nouveauxFiltres ->
                    presentateur.filtrerParFiltres(nouveauxFiltres)
                }
            }
            panneau.show(supportFragmentManager, "filtres")
        }

        // ── 6. Charger toutes les recettes au démarrage ──
        presentateur.chargerRecettes()

        // ── 7. Chips session via le présentateur ──
        presentateur.chargerSessionIngredients()
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

    /**
     * Affiche les ingrédients de la dernière session de l'utilisateur
     * sous forme de chips dans l'encadré hero de l'accueil.
     * Reçoit une liste de noms depuis le présentateur — la Vue ne
     * touche pas aux repositories directement (respect du MVP).
     * Affiche au maximum 3 chips.
     */
    override fun afficherChipsSession(noms: List<String>) {
        binding.chipGroupSessionIngredients.removeAllViews()
        if (noms.isEmpty()) return
        noms.forEach { nom ->
            val chip = Chip(this).apply {
                text = "✓ $nom"
                textSize = 10f
                isClickable = false
                isCheckable = false
                chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(this@AccueilActivity, R.color.teal_100)
                )
                setTextColor(ContextCompat.getColor(this@AccueilActivity, R.color.teal_900))
                // chipMinHeight en dp pour un rendu cohérent sur tous les écrans
                chipMinHeight = (28 * resources.displayMetrics.density)
            }
            binding.chipGroupSessionIngredients.addView(chip)
        }
    }

    private fun naviguerDetail(id: Long) {
        val intent = Intent(this, DetailRecetteActivity::class.java)
        intent.putExtra("recette_id", id)
        // Indique que la navigation vient de l'Accueil — pas d'ingrédients disponibles
        // DetailRecetteActivity cachera les icônes possédé/manquant en conséquence
        intent.putExtra("depuis_accueil", true)
        startActivity(intent)
    }

    /**
     * Construit un FiltreRecette combiné à partir de l'état actuel des chips
     * et appelle le présentateur pour filtrer les recettes.
     * Permet de combiner plusieurs filtres simultanément (ex: Facile + Végé).
     * Si aucun filtre n'est actif, recharge toutes les recettes.
     */
    private fun appliquerFiltresCombines() {
        val filtres = FiltreRecette(
            difficulte = filtreDifficulte,
            isVege     = filtreIsVege,
            tempsMax   = filtreTempsMax
        )
        if (filtreDifficulte == null && !filtreIsVege && filtreTempsMax == null) {
            presentateur.chargerRecettes()
        } else {
            presentateur.filtrerParFiltres(filtres)
        }
    }
}