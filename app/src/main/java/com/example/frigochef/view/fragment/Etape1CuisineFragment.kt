package com.example.frigochef.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.frigochef.R
import com.example.frigochef.databinding.FragmentEtape1QuestionnaireBinding


class Etape1CuisineFragment: Fragment(R.layout.fragment_etape1_questionnaire){

    private var _binding: FragmentEtape1QuestionnaireBinding? = null
    private val binding get() = _binding!!

    private val cuisines = listOf(
        Pair("Italienne",       "🍝"),
        Pair("Mexicaine",       "🌮"),
        Pair("Indienne",        "🍛"),
        Pair("Japonaise",       "🍜"),
        Pair("Grecque",         "🥗"),
        Pair("Québécoise",      "🍁"),
        Pair("Africaine",       "🌍"),
        Pair("Méditerranéenne", "🫒"),
        Pair("Américaine",      "🍔"),
        Pair("Moyen-Orientale", "🧆")
    )

    private val selectionnees = mutableSetOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentEtape1QuestionnaireBinding.bind(view)

        construireGrille()

        val activity = requireActivity() as QuestionnaireActivity
        selectionnees.addAll(activity.cuisinesSelectionnees)

        rafraichirAffichage()
    }

    private fun construireGrille(){

    }

    // Doit parcourir toutes les cartes et mettre a jour leurs couleurs
    private fun rafraichirAffichage(){

    }
}