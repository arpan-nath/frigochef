package com.example.frigochef.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.frigochef.R
import com.example.frigochef.databinding.FragmentEtape4QuestionnaireBinding
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.view.QuestionnaireActivity
import com.google.android.material.chip.Chip
/**
 * Quatrième étape du questionnaire — récapitulatif des sélections avant soumission.
 * Lit l'état complet depuis QuestionnaireActivity dans onResume() pour refléter
 * les modifications si l'utilisateur revient en arrière depuis cette étape.
 * Affiche les cuisines, contraintes, régimes et ingrédients sélectionnés sous forme
 * de chips non cliquables. Résout les noms d'ingrédients via IngredientRepository.findByIds()
 * et formate les quantités en supprimant les décimales inutiles (1.0 → "1").
 * Affiche "Peu importe" ou "Aucun" pour chaque section sans sélection.
 */
class Etape4RecapFragment: Fragment(R.layout.fragment_etape4_questionnaire){

    private var _binding: FragmentEtape4QuestionnaireBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEtape4QuestionnaireBinding.bind(view)
    }

    override fun onResume(){
        super.onResume()
        afficherRecap()
    }

    private fun afficherRecap(){
        val activity = requireActivity() as QuestionnaireActivity

        afficherCuisines(activity.cuisinesSelectionnees)
        afficherContraintes(activity.filtres)
        afficherRegime(activity.filtres)
        afficherIngredients(activity)
        afficherMessage(activity)
    }

    private fun afficherCuisines(cuisines: List<String>){
        binding.chipGroupRecapCuisine.removeAllViews()
        val labels = cuisines.ifEmpty { listOf("Peu importe") }
        labels.forEach { nom->ajouterChip(binding.chipGroupRecapCuisine, nom) }
    }

    private fun afficherContraintes(filtres: FiltreRecette){
        binding.chipGroupRecapContraintes.removeAllViews()

        val labels = mutableListOf<String>()

        filtres.typeRepas?.let{labels.add(it)}
        filtres.difficulte?.let{labels.add(it)}
        filtres.tempsMax?.let{labels.add("Max $it min")}

        if(labels.isEmpty()) labels.add("Aucune")
        labels.forEach{label ->ajouterChip(binding.chipGroupRecapContraintes, label)}

    }

    private fun afficherRegime(filtres: FiltreRecette){
        binding.chipGroupRecapRegime.removeAllViews()
        val labels = mutableListOf<String>()

        if(filtres.isVege) labels.add("Végétarien")
        if(filtres.isVegan) labels.add("Végane")
        if(filtres.isSansGluten) labels.add("Sans gluten")

        if(!filtres.isVege && !filtres.isVegan && !filtres.isSansGluten) labels.add("Aucun")
        labels.forEach{label ->ajouterChip(binding.chipGroupRecapRegime, label)}
    }

    private fun afficherIngredients(activity: QuestionnaireActivity){
        binding.chipGroupRecapIngredients.removeAllViews()

        if(activity.ingredientsQuantites.isEmpty()){
            ajouterChip(binding.chipGroupRecapIngredients, "Aucun ingrédients")
            return
        }

        val repo = IngredientRepository(requireContext())
        val ids  = activity.ingredientsQuantites.map { it.ingredientId }
        val noms = repo.findByIds(ids).associateBy { it.id }

        activity.ingredientsQuantites.forEach { iq ->

            val nom = noms[iq.ingredientId]?.nom ?: return@forEach

            val quantiteStr = if(iq.quantite == iq.quantite.toLong().toDouble())
                iq.quantite.toLong().toString()
            else
                iq.quantite.toString()

            ajouterChip(binding.chipGroupRecapIngredients, "$quantiteStr ${iq.unite} $nom")
        }
    }

    private fun afficherMessage(activity: QuestionnaireActivity){
        val count = activity.ingredientsQuantites.size
        binding.tvRecapMessage.text = when (count){
            0 -> "On va trouver les meilleures recettes selon vos critères."
            else -> "On va trouver les meilleures recettes avec vos $count ingrédients."
        }
    }

    private fun ajouterChip(
        groupe: com.google.android.material.chip.ChipGroup,
        texte: String
    ) {
        val chip = Chip(requireContext()).apply {

            text = texte
            isCheckable = false  //peut pas etre coché
            isClickable = false  // peut pas etre cliqué
            isCheckedIconVisible = false  // pas d'icone
            textSize = 11f

            chipBackgroundColor  = resources.getColorStateList(
                R.color.chip_background_selector, null
            )

        }
        groupe.addView(chip)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}