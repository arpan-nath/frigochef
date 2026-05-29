package com.example.frigochef.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.frigochef.R
import com.example.frigochef.databinding.FragmentEtape1QuestionnaireBinding
import com.example.frigochef.databinding.ItemLayoutCuisineBinding
import com.example.frigochef.view.QuestionnaireActivity
/**
 * Première étape du questionnaire — sélection du type de cuisine.
 * Construit dynamiquement une grille de 10 cartes (MaterialCardView) via LayoutInflater
 * à partir d'une liste locale de paires (nom, icône).
 * Chaque clic bascule l'état sélectionné/désélectionné et met à jour la couleur de la carte.
 * Synchronise cuisinesSelectionnees dans QuestionnaireActivity à chaque interaction
 * pour que l'état soit accessible par tous les fragments.
 * Restaure les sélections précédentes si l'utilisateur revient en arrière depuis l'étape 2.
 */
class Etape1CuisineFragment: Fragment(R.layout.fragment_etape1_questionnaire){

    private var _binding: FragmentEtape1QuestionnaireBinding? = null
    private val binding get() = _binding!!

    private val cuisines = listOf(
        Pair("Italienne",       "🍝"),
        Pair("Mexicaine",       "🌮"),
        Pair("Indienne",        "🍛"),
        Pair("Japonaise",       "🍜"),
        Pair("Grecque",         "🥙"),
        Pair("Québécoise",      "🥧"),
        Pair("Africaine",       "🥘"),
        Pair("Méditerranéenne", "🥗"),
        Pair("Américaine",      "🍔"),
        Pair("Moyen-Orientale", "🧆")
    )


    private val selectionnees = mutableSetOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentEtape1QuestionnaireBinding.bind(view)

        construireGrille()

        // Pour si l'utilisateur veut revenir en arriere depuis l'étape 2, on récupère ses selections
        val activity = requireActivity() as QuestionnaireActivity
        selectionnees.addAll(activity.cuisinesSelectionnees)

        rafraichirAffichage()
    }

    // Code généré à l'aide de Claude AI
    /*
    Cette méthode crée une grille de 10 cartes de recette
    */
    private fun construireGrille(){

        // LayoutInflater transforme un fichier XML en vrai objet View
        val inflater = LayoutInflater.from(requireContext())

        // Pour chaque cuisine dans notre liste, on crée une carte
        cuisines.forEach{ (nom, icone) ->

            val carteBinding = ItemLayoutCuisineBinding.inflate(
                inflater,
                binding.gridCuisines,
                false
            )

            carteBinding.tvIconeCuisine.text = icone
            carteBinding.tvNomCuisine.text   = nom

            // GridLayout.LayoutParams contrôle comment chaque carte s'étale dans la grille
            val params = GridLayout.LayoutParams().apply{
                width       = 0
                columnSpec  = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec     = GridLayout.spec(GridLayout.UNDEFINED, 1f) // 1f fait que chaque carte prend la même largeur
            }
            carteBinding.root.layoutParams = params

            // Quand l'utilisateur clique sur une carte
            carteBinding.root.setOnClickListener{

                if(selectionnees.contains(nom)){ // si cest déja sélectionné, déselectionne
                    selectionnees.remove(nom)
                }else{ // sinon, sélectionne
                    selectionnees.add(nom)
                }

                // on met a jour l'apparence de la carte
                mettreAJourCarte(carteBinding, nom)
                // on sauvegarde dans l'activity pour que les quatres fragments puissent y accéder
                // car, Activity est le point central de communication entre fragments
                (requireActivity() as QuestionnaireActivity).cuisinesSelectionnees =
                    selectionnees.toList()
            }
            // On ajoute la carte dans le GridLayout du XML
            binding.gridCuisines.addView(carteBinding.root)
        }

        }

    // Cette méthode parcours toutes les cartes et met a jour leurs couleurs
    private fun rafraichirAffichage(){
        val count = binding.gridCuisines.childCount
        for(i in 0 until count){
            val carteView = binding.gridCuisines.getChildAt(i)

            val cardView = carteView as? com.google.android.material.card.MaterialCardView ?: continue

            val nom = cuisines[i].first
            val selectionne = selectionnees.contains(nom)

            cardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (selectionne) R.color.cuisine_teal_bg else R.color.white
                )

            )
            cardView.strokeColor = ContextCompat.getColor(
                requireContext(),
                if (selectionne) R.color.teal_400 else R.color.border_tertiary
            )
            cardView.strokeWidth = if (selectionne) 3 else 1

            // Changer la couleur du texte selon l'état
            // On accède au TextView à l'intérieur de la carte via findViewById
            val tvNom = cardView.findViewById<android.widget.TextView>(R.id.tvNomCuisine)
            tvNom?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if(selectionne) R.color.teal_800 else R.color.text_primary
                )

            )
        }
    }

    // Met à jour l'apparence d'une seule carte apres un clic
    private fun mettreAJourCarte(carteBinding: ItemLayoutCuisineBinding, nom: String){
        val selectionne = selectionnees.contains(nom)

        // Changer la couleur de fond de la CardView
        carteBinding.root.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (selectionne) R.color.cuisine_teal_bg else R.color.white
            )
        )

        // Changer aussi la couleur du texte pour mieux distinguer
        carteBinding.tvNomCuisine.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selectionne) R.color.teal_800 else R.color.text_primary
            )
        )

        // Ajouter une bordure verte quand sélectionné
        carteBinding.root.strokeColor = ContextCompat.getColor(
            requireContext(),
            if (selectionne) R.color.teal_400 else R.color.border_tertiary
        )
        carteBinding.root.strokeWidth = if (selectionne) 3 else 1
    }

    // onDestroy est appelé quand le fragment quitte l'écran
    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null  // on met binding a null pour libérer de la mémoire
    }

}