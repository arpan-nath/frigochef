package com.example.frigochef.view.fragment

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.frigochef.R
import com.example.frigochef.databinding.FragmentEtape3QuestionnaireBinding
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.view.QuestionnaireActivity
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frigochef.databinding.ItemSaisiIngredientBinding
import com.example.frigochef.databinding.ItemSuggestionIngredientBinding
import com.example.frigochef.model.entity.Ingredient
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.view.adapter.IngredientSaisiAdapter
import com.example.frigochef.view.adapter.SuggestionIngredientAdapter
import com.google.android.material.chip.Chip
/**
 * Troisième étape du questionnaire — saisie des ingrédients disponibles.
 * Affiche les ingrédients de la session précédente sous forme de chips cliquables
 * en lisant idsSessionPrecedente depuis QuestionnaireActivity dans onViewCreated()
 * (inversion de responsabilité pour contourner le problème de timing du ViewPager2).
 * Offre une barre de recherche avec TextWatcher qui délègue au présentateur via
 * rechercherIngredient(), et affiche les suggestions dans un RecyclerView.
 * Maintient la liste ingredientsSaisis avec quantité et unité par défaut via
 * findUniteParDefaut(). Synchronise ingredientsQuantites dans QuestionnaireActivity
 * avant d'appeler presenter.valider() au clic sur "Voir les recettes".
 */
class Etape3IngredientsFragment: Fragment(R.layout.fragment_etape3_questionnaire){

    private var _binding: FragmentEtape3QuestionnaireBinding? = null
    private val binding get() = _binding!!

    private val ingredientsSaisis = mutableListOf<IngredientQuantite>()

    private val ingredientsCache = mutableMapOf<Long, Ingredient>()


    private lateinit var ingredientRepository: IngredientRepository

    private lateinit var suggestionAdapter: SuggestionIngredientAdapter

    private lateinit var saisiAdapter: IngredientSaisiAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEtape3QuestionnaireBinding.bind(view)

        ingredientRepository = IngredientRepository(requireContext())

        configurerRecherche()
        configurerRecyclerViews()
        configurerBoutonVoirRecettes()

        val ids = (requireActivity() as QuestionnaireActivity).idsSessionPrecedente
        if (ids.isNotEmpty()) {
            precacherIngredients(ids)
        }
    }



    private fun configurerRecyclerViews(){
        suggestionAdapter = SuggestionIngredientAdapter { ingredient ->
            ajouterIngredient(ingredient)
        }
        saisiAdapter = IngredientSaisiAdapter(
            onQuantiteChange = { id, quantite ->
                val index = ingredientsSaisis.indexOfFirst { it.ingredientId == id }
                if(index >= 0){
                    ingredientsSaisis[index] = ingredientsSaisis[index].copy(quantite = quantite)
                }
            },
            onSupprimer = { id -> supprimerIngredient(id) }
        )
        binding.rvSuggestionsRecherche.adapter = suggestionAdapter
        binding.rvSuggestionsRecherche.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngredientsSaisis.adapter = saisiAdapter
        binding.rvIngredientsSaisis.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun configurerRecherche(){
        binding.etRechercheIngredient.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?,start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){}
            override fun afterTextChanged(s: Editable?){
                val query = s.toString().trim()

                if(query.isEmpty()){
                    binding.rvSuggestionsRecherche.visibility = View.GONE
                }else{
                    (requireActivity() as QuestionnaireActivity)
                        .presenter.rechercherIngredient(query)
                }
            }

        })
    }


    private fun configurerBoutonVoirRecettes(){
        mettreAJourBouton()

        binding.btnVoirRecettes.setOnClickListener{
            val activity = requireActivity() as QuestionnaireActivity
            activity.ingredientsQuantites.clear()
            activity.ingredientsQuantites.addAll(ingredientsSaisis)


            activity.sauvegarderPreferences()

            val ids = ingredientsSaisis.map { it.ingredientId }
            activity.presenter.valider(activity.filtres, ids, activity.cuisinesSelectionnees)
        }
    }

    // maj du texte du bouton avec le nombre d'ingrédients saisis
    private fun mettreAJourBouton(){
        val count = ingredientsSaisis.size
        binding.btnVoirRecettes.text = when (count){
            0 -> "Voir les recettes"
            1 -> "Voir les recettes (1 ingrédient)"
            else -> "Voir les recettes ($count ingrédients)"
        }

    }

    // affiche les suggestions retournées par le presenter
    fun afficherSuggestions(ingredients: List<Ingredient>){
        if(ingredients.isEmpty()){
            binding.rvSuggestionsRecherche.visibility = View.GONE
            return
        }
        binding.rvSuggestionsRecherche.visibility = View.VISIBLE
        suggestionAdapter.soumettre(ingredients)
    }


    // Recoit les ids de la session précédente et affiche les chips
    fun precacherIngredients(ids: List<Long>){

        if(ids.isEmpty()) return

        val ingredients = ingredientRepository.findByIds(ids)
        if (ingredients.isEmpty()) return


        // Afficher la section session précédente
        binding.layoutSessionPrecedente.visibility = View.VISIBLE
        binding.chipGroupSessionPrecedente.removeAllViews()

        ingredients.forEach { ingredient ->
            ingredientsCache[ingredient.id] = ingredient

            // Créer le ✕
            val chip = Chip(requireContext()).apply {
                text = "✕ ${ingredient.nom}"
                isCheckable = false
                textSize = 11f
                chipBackgroundColor = resources.getColorStateList(
                    R.color.chip_background_selector, null
                )
            }
            // ingrédient s'ajoute à la liste quand on clic sur le chip
            chip.setOnClickListener{
                ajouterIngredient(ingredient)
                binding.chipGroupSessionPrecedente.removeView(chip)

                // Cacher la section si plus aucun chip
                if(binding.chipGroupSessionPrecedente.childCount == 0){
                    binding.layoutSessionPrecedente.visibility = View.GONE
                }
            }
            binding.chipGroupSessionPrecedente.addView(chip)
        }
    }

    private fun ajouterIngredient(ingredient: Ingredient){
        if(ingredientsSaisis.any { it.ingredientId == ingredient.id }){
            binding.rvSuggestionsRecherche.visibility = View.GONE
            binding.etRechercheIngredient.setText("")
            return
        }

        // Récupérer l'unité la plus fréquente pour cet ingrédient dans la BD
        val unite = ingredientRepository.findUniteParDefaut(ingredient.id)

        ingredientsSaisis.add(
            IngredientQuantite(
                ingredientId = ingredient.id,
                quantite     = 1.0,
                unite        = unite
            )
        )
        ingredientsCache[ingredient.id] = ingredient

        // Cacher le dropdown et vider le champ de recherche
        binding.rvSuggestionsRecherche.visibility = View.GONE
        binding.etRechercheIngredient.setText("")

        // Afficher la section et rafraîchir
        binding.layoutIngredientsSaisis.visibility = View.VISIBLE
        rafraichirListeSaisie()
        mettreAJourBouton()
    }

    private fun supprimerIngredient(ingredientId: Long){
        ingredientsSaisis.removeAll { it.ingredientId == ingredientId }
        if(ingredientsSaisis.isEmpty()){
            binding.layoutIngredientsSaisis.visibility = View.GONE
        }
        rafraichirListeSaisie()
        mettreAJourBouton()
    }


    private fun rafraichirListeSaisie(){
        val count = ingredientsSaisis.size
        binding.tvTitreFrigo.text = "DANS MON FRIGO ($count)"
        saisiAdapter.soumettre(ingredientsSaisis.toList(), ingredientsCache)
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }




}