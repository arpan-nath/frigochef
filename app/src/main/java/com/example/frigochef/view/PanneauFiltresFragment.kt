package com.example.frigochef.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frigochef.databinding.FragmentPanneauFiltresBinding
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.IngredientQuantite
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

/**
 * BottomSheet réutilisable pour sélectionner les filtres de recherche.
 * Utilisé dans ResultatsActivity et AccueilActivity.
 * Retourne un FiltreRecette complet via le callback onFiltresAppliques.
 */

class PanneauFiltresFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPanneauFiltresBinding

    var onFiltresAppliques: ((FiltreRecette) -> Unit)? = null

    var filtresActuels:    FiltreRecette           = FiltreRecette()
    var ingredientsDispos: List<IngredientQuantite> = emptyList()
    var nomsIngredients:   Map<Long, String>        = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPanneauFiltresBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Cette fonction est appelée juste après la création de la vue. Elle s'occupe d'initialiser
    // l'affichage (génération des puces, restauration des filtres) et de configurer les écouteurs
    // d'événements sur les boutons et le slider.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        genererChipsCuisine()
        afficherIngredientsSaisis()
        restaurerFiltres()

        binding.btnFermerPanel.setOnClickListener {
            dismiss()
        }

        binding.btnReinitialiser.setOnClickListener {
            reinitialiserFiltres()
        }

        binding.btnAppliquer.setOnClickListener {
            val filtres = lireFiltres()
            onFiltresAppliques?.invoke(filtres)
            dismiss()
        }

        binding.sliderTempsMax.addOnChangeListener { _, value, _ ->
            binding.tvTempsMaxValeur.text = "${value.toInt()} min"
        }
    }

    // Cette fonction crée dynamiquement des éléments visuels "Chip" pour une liste prédéfinie de
    // types de cuisine et les ajoute au groupe de puces (ChipGroup) correspondant dans l'interface.
    private fun genererChipsCuisine() {
        val cuisines = listOf(
            "Moyen-Orientale", "Méditerranéenne", "Italienne",
            "Grecque",         "Américaine",      "Indienne",
            "Japonaise",       "Mexicaine",       "Québécoise", "Africaine"
        )
        binding.chipGroupTypeCuisine.removeAllViews()
        cuisines.forEach { cuisine ->
            val chip = Chip(requireContext()).apply {
                text                 = cuisine
                isCheckable          = true
                isCheckedIconVisible = false
                isChecked            = filtresActuels.typeCuisine == cuisine
                textSize             = 10f
                setChipBackgroundColorResource(com.example.frigochef.R.color.chip_background_selector)
            }
            binding.chipGroupTypeCuisine.addView(chip)
        }
    }

    private fun afficherIngredientsSaisis() {
        binding.chipGroupMesIngredients.removeAllViews()
        ingredientsDispos.forEach { iq ->
            val nom  = nomsIngredients[iq.ingredientId] ?: "Ingrédient ${iq.ingredientId}"
            val chip = Chip(requireContext()).apply {
                text        = "$nom (${iq.quantite.toInt()} ${iq.unite})"
                isCheckable = false
                textSize    = 10f
            }
            binding.chipGroupMesIngredients.addView(chip)
        }
    }

    // Cette fonction met à jour l'interface utilisateur en cochant les puces appropriées et en
    // ajustant le curseur de temps selon l'état de l'objet 'filtresActuels'
    // (pour garder les filtres en mémoire lors de la réouverture du panneau).
    private fun restaurerFiltres() {
        // Type de repas
        binding.chipDejeuner.isChecked  = filtresActuels.typeRepas == "Déjeuner"
        binding.chipDiner.isChecked     = filtresActuels.typeRepas == "Dîner"
        binding.chipSouper.isChecked    = filtresActuels.typeRepas == "Souper"
        binding.chipCollation.isChecked = filtresActuels.typeRepas == "Collation"

        // Difficulté
        binding.chipFacile.isChecked    = filtresActuels.difficulte == "Facile"
        binding.chipMoyen.isChecked     = filtresActuels.difficulte == "Moyen"
        binding.chipDifficile.isChecked = filtresActuels.difficulte == "Difficile"

        // Régime
        binding.chipVegetarien.isChecked = filtresActuels.isVege
        binding.chipVegan.isChecked      = filtresActuels.isVegan
        binding.chipSansGluten.isChecked = filtresActuels.isSansGluten

        // Temps max
        val tempsMax = filtresActuels.tempsMax ?: 120
        binding.sliderTempsMax.value  = tempsMax.toFloat()
        binding.tvTempsMaxValeur.text = "$tempsMax min"
    }

    // Cette fonction inspecte l'état actuel de tous les éléments interactifs de l'interface
    // (puces cochées et valeur du slider) afin de construire et renvoyer un nouvel objet FiltreRecette représentant les choix de l'utilisateur.
    private fun lireFiltres(): FiltreRecette {

        // Type de cuisine — lire le chip coché dynamiquement
        val typeCuisine = (0 until binding.chipGroupTypeCuisine.childCount)
            .map { binding.chipGroupTypeCuisine.getChildAt(it) as Chip }
            .firstOrNull { it.isChecked }
            ?.text?.toString()

        // Type de repas
        val typeRepas = when {
            binding.chipDejeuner.isChecked  -> "Déjeuner"
            binding.chipDiner.isChecked     -> "Dîner"
            binding.chipSouper.isChecked    -> "Souper"
            binding.chipCollation.isChecked -> "Collation"
            else                            -> null
        }

        // Difficulté
        val difficulte = when {
            binding.chipFacile.isChecked    -> "Facile"
            binding.chipMoyen.isChecked     -> "Moyen"
            binding.chipDifficile.isChecked -> "Difficile"
            else                            -> null
        }

        // Temps max — 120 = pas de filtre
        val tempsMax = binding.sliderTempsMax.value.toInt()
            .takeIf { it < 120 }

        return FiltreRecette(
            typeCuisine  = typeCuisine,
            typeRepas    = typeRepas,
            difficulte   = difficulte,
            tempsMax     = tempsMax,
            isVege       = binding.chipVegetarien.isChecked,
            isVegan      = binding.chipVegan.isChecked,
            isSansGluten = binding.chipSansGluten.isChecked
        )
    }

    // Cette fonction permet de réinitialiser l'affichage en décochant l'ensemble des puces dans
    // tous les groupes et en remettant le slider de temps maximum à sa valeur d'origine (120 min).
    private fun reinitialiserFiltres() {
        binding.chipGroupTypeRepas.clearCheck()
        binding.chipGroupDifficulte.clearCheck()
        binding.chipGroupRegime.clearCheck()
        binding.chipGroupTypeCuisine.clearCheck()
        binding.sliderTempsMax.value  = 120f
        binding.tvTempsMaxValeur.text = "120 min"
    }
}