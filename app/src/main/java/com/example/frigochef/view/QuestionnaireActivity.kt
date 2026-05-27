package com.example.frigochef.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.frigochef.contract.QuestionnaireContract
import com.example.frigochef.databinding.ActivityQuestionnaireBinding
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.model.repository.SessionRepository
import com.example.frigochef.presenter.QuestionnairePresenter
import com.example.frigochef.view.adapter.QuestionnairePagerAdapter
import androidx.viewpager2.widget.ViewPager2
import android.view.View
import com.example.frigochef.model.PrefsManager
import com.example.frigochef.model.entity.Ingredient
import com.example.frigochef.view.fragment.Etape3IngredientsFragment

class QuestionnaireActivity: AppCompatActivity(), QuestionnaireContract.View {

    private lateinit var binding: ActivityQuestionnaireBinding
    lateinit var presenter: QuestionnairePresenter
    private lateinit var adapter: QuestionnairePagerAdapter

    private lateinit var prefsManager: PrefsManager

    // État partagé entre les fragments
    var cuisinesSelectionnees: List<String> = emptyList()
    var filtres: FiltreRecette = FiltreRecette()
    var ingredientsQuantites: MutableList<IngredientQuantite> = mutableListOf()

    private val etapeMax = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionnaireBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = QuestionnairePresenter(
            vue = this,
            ingredientRepository = IngredientRepository(this),
            sessionRepository = SessionRepository(this)
        )

        adapter = QuestionnairePagerAdapter(this)
        binding.viewPagerEtapes.adapter = adapter
        // Désactive le glissement (navigation avec boutons seulement)
        binding.viewPagerEtapes.isUserInputEnabled = false

        mettreAJourUI(0)

        binding.btnRetour.setOnClickListener{etapePrecedente()}
        binding.btnContinuer.setOnClickListener{etapeSuivante()}
        binding.tvIgnorer.setOnClickListener{etapeSuivante()}

        // Barre de progression change selon la page actuelle
        binding.viewPagerEtapes.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int){
                    mettreAJourUI(position)

                }

            }
        )

        presenter.chargerSessionPrecedente()

        prefsManager = PrefsManager(this)

        filtres = prefsManager.chargerFiltres()
        cuisinesSelectionnees = prefsManager.chargerCuisines()
    }

    fun sauvegarderPreferences(){
        prefsManager.sauvegarderFiltres(filtres, cuisinesSelectionnees)
    }

    // navigation entre étapes
    private fun etapeSuivante(){
        val etapeActuelle = binding.viewPagerEtapes.currentItem
        if(etapeActuelle < etapeMax){
            binding.viewPagerEtapes.currentItem = etapeActuelle + 1
        }else{
            if(cuisinesSelectionnees.isNotEmpty()){
                filtres = filtres.copy(typeCuisine = cuisinesSelectionnees.first())
            }
            // (étape 4 navigue vers les résultats)
            sauvegarderPreferences()
            val ids = ingredientsQuantites.map { it.ingredientId }
            presenter.valider(filtres, ids)

        }
    }

    private fun etapePrecedente(){
        val etapeActuelle = binding.viewPagerEtapes.currentItem
        if(etapeActuelle > 0){
            binding.viewPagerEtapes.currentItem = etapeActuelle - 1

        }else{
            finish() // retour à accueil
        }
    }

    // Code généré à l'aide de Claude AI
    // Adapte le bouton et la barre de progression selon l'étape actuelle
    private fun mettreAJourUI(position: Int){
        val numero = position + 1
        val progression = (numero * 100) / (etapeMax + 1)
        binding.progressBarEtapes.progress = progression
        binding.tvEtapeLabel.text = "$numero / ${etapeMax + 1}"

        when(position){
            // Fragment 3 a son propre bouton "Voir les recettes"
            2 -> {
                binding.btnContinuer.visibility = View.GONE
                binding.tvIgnorer.visibility = View.GONE
            }

            // fragment 4 change le texte du bouton
            3 -> {
                binding.btnContinuer.text = "Voir mes recettes"
                binding.btnContinuer.visibility = View.VISIBLE
                binding.tvIgnorer.visibility = View.GONE
            }
            // fragment 1 et 2 ont un bouton normal + lien ignorer
            else -> {
                binding.btnContinuer.text = "→ Continuer"
                binding.btnContinuer.visibility = View.VISIBLE
                binding.tvIgnorer.visibility = View.VISIBLE
            }
        }

    }

    override fun afficherIngredientsSuggeres(ingredients: List<Ingredient>){
        val fragment = supportFragmentManager.findFragmentByTag("f2")
        (fragment as? Etape3IngredientsFragment)?.afficherSuggestions(ingredients)
    }

    override fun afficherIngredientsPrecaches(ids: List<Long>){
        val fragment = supportFragmentManager.findFragmentByTag("f2")
        (fragment as? Etape3IngredientsFragment)?.precacherIngredients(ids)
    }

    override fun naviguerVersResultats(filtres: FiltreRecette,ingredientsCoches: List<Long>){
        val intent = Intent(this, ResultatsActivity::class.java).apply{
            putExtra("filtres",      filtres)
            putExtra("ingredients",  ArrayList(ingredientsQuantites))
        }
        startActivity(intent)
    }

}
