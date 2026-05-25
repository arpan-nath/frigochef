package com.example.frigochef.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import com.example.frigochef.R
import com.example.frigochef.databinding.FragmentEtape1QuestionnaireBinding
import com.example.frigochef.databinding.ItemCarteCuisineBinding
import com.example.frigochef.view.QuestionnaireActivity

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
        val inflater = LayoutInflater.from(requireContext())

        cuisines.forEach{ (nom, icone) ->

            val carteBinding = ItemCarteCuisineBinding.inflate(
                inflater,
                binding.gridCuisines,
                false
            )

            carteBinding.tvIconeCuisine.text = icone
            carteBinding.tvNomCuisine.text   = nom

            // Configurer les parametres pour le gridLayout
            val params = GridLayout.LayoutParams().apply{
                width       = 0
                columnSpec  = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec     = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            carteBinding.root.layoutParams = params

            carteBinding.root.setOnClickListener{

                if(selectionnees.contains(nom)){
                    selectionnees.remove(nom)
                }else{
                    selectionnees.add(nom)
                }
                mettreAJourCarte(carteBinding, nom)
                (requireActivity() as QuestionnaireActivity).cuisinesSelectionnees =
                    selectionnees.toList()
            }

            binding.gridCuisines.addView(carteBinding.root)
        }

        }

    // Doit parcourir toutes les cartes et mettre a jour leurs couleurs
    private fun rafraichirAffichage(){

    }
}