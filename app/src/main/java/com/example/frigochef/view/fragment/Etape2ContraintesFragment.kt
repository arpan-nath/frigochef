package com.example.frigochef.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.frigochef.R
import com.example.frigochef.databinding.FragmentEtape2QuestionnaireBinding
import com.example.frigochef.view.QuestionnaireActivity
import com.google.android.material.slider.Slider

class Etape2ContraintesFragment: Fragment(R.layout.fragment_etape2_questionnaire){
    private var _binding: FragmentEtape2QuestionnaireBinding? = null
    private val binding get() = _binding!!
    private var sliderModifie = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEtape2QuestionnaireBinding.bind(view)

        restaurerFiltres()

        configurerChips()
        configurerSlider()
    }

    private fun restaurerFiltres(){
        val filtres = (requireActivity() as QuestionnaireActivity).filtresInitiaux


        // Cocher les chips type de repas
        when(filtres.typeRepas){
            "Déjeuner" -> binding.chipDejeuner.isChecked = true
            "Dîner" -> binding.chipDiner.isChecked = true
            "Souper" -> binding.chipSouper.isChecked = true
            "Collation" -> binding.chipCollation.isChecked = true
        }

        // Cocher les chips difficulté
        when(filtres.difficulte){
            "Facile" -> binding.chipFacile.isChecked = true
            "Moyen" -> binding.chipMoyen.isChecked = true
            "Difficile" -> binding.chipDifficile.isChecked = true
        }

        // Cocher les chips régime
        binding.chipVegetarien.isChecked = filtres.isVege
        binding.chipVegan.isChecked = filtres.isVegan
        binding.chipSansGluten.isChecked = filtres.isSansGluten

        // slider restauré seulement si une valeur avait été explicitement sauvegardée
        if(filtres.tempsMax != null){
            binding.sliderTempsMax.value = filtres.tempsMax.toFloat()
            binding.tvTempsMaxValeur.text = "${filtres.tempsMax} min"
            sliderModifie = true
        }else{
            binding.tvTempsMaxValeur.text = "${binding.sliderTempsMax.value.toInt()} min"
            sliderModifie = false  // pas de valeur sauvegardée → slider ignoré
        }
    }

    // logique: chaque fois qu'un chip change d'état, on reconstruit le FiltreRecette et on le sauvegarde dans l'Activity
    private fun configurerChips(){
        binding.chipGroupTypeRepas.setOnCheckedStateChangeListener { group, checkedIds ->
            sauvegarderFiltres()
        }

        binding.chipGroupDifficulte.setOnCheckedStateChangeListener { _, _ ->
            sauvegarderFiltres()
        }

        binding.chipGroupRegime.setOnCheckedStateChangeListener { _, _ ->
            sauvegarderFiltres()
        }


    }

    // Code généré à l'aide de Claude AI
    // .OnChangeListener est une interface qui écoute les changements de valeur
    // value contient la valeur actuelle du slider (entre 5 et 120)
    private fun configurerSlider(){
        binding.sliderTempsMax.addOnChangeListener(Slider.OnChangeListener { _, value, fromUser ->
            binding.tvTempsMaxValeur.text = "${value.toInt()} min"

            if(fromUser) sliderModifie = true
            sauvegarderFiltres()
        })
    }

    // Code généré à l'aide de Claude AI
    // Lit l'état actuel de tous les chips et du slider, construit un FiltreRecette puis le sauvegarde
    private fun sauvegarderFiltres(){
        val typeRepas = when{
            binding.chipDejeuner.isChecked -> "Déjeuner"
            binding.chipDiner.isChecked -> "Dîner"
            binding.chipSouper.isChecked -> "Souper"
            binding.chipCollation.isChecked -> "Collation"
            else -> null
        }

        val difficulte = when{
            binding.chipFacile.isChecked -> "Facile"
            binding.chipMoyen.isChecked -> "Moyen"
            binding.chipDifficile.isChecked -> "Difficile"
            else -> null
        }

        val tempsMax = if (sliderModifie) binding.sliderTempsMax.value.toInt() else null

        val activity = requireActivity() as QuestionnaireActivity
        activity.filtres = activity.filtres.copy(
            typeRepas = typeRepas,
            difficulte = difficulte,
            tempsMax = tempsMax,
            isVege = binding.chipVegetarien.isChecked,
            isVegan = binding.chipVegan.isChecked,
            isSansGluten = binding.chipSansGluten.isChecked

        )

    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }

}