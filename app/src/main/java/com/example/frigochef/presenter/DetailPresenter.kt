package com.example.frigochef.presenter

import com.example.frigochef.contract.DetailContract
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.repository.RecetteRepository

/**
 * Présentateur de l'écran de détail d'une recette.
 * Charge la recette et ses ingrédients depuis le repository,
 * puis délègue l'affichage à la vue.
 */

class DetailPresenter(
    private val vue:        DetailContract.View,
    private val repository: RecetteRepository
) : DetailContract.Presenter {

    override fun chargerDetail(
        recetteId:         Long,
        ingredientsDispos: List<IngredientQuantite>
    ) {
        val recette = repository.findById(recetteId)
        if (recette == null) {
            vue.afficherErreur("Recette introuvable.")
            return
        }

        val ingredients = repository.findIngredientsDetailParRecette(recetteId)

        vue.afficherRecette(recette)
        vue.afficherIngredients(ingredients, ingredientsDispos)
    }
}