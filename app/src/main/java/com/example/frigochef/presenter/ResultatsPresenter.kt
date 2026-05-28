package com.example.frigochef.presenter

import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.RecetteAvecScore
import com.example.frigochef.model.repository.RecetteRepository
import com.example.frigochef.model.ScoreCalculateur
import com.example.frigochef.contract.ResultatsContract
import com.example.frigochef.model.entity.IngredientQuantite

/**
 * Présentateur de l'écran des résultats.
 * Récupère les recettes filtrées, calcule le score de compatibilité
 * pour chacune via ScoreCalculateur, puis trie par score décroissant.
 */

class ResultatsPresenter(
    private val vue:        ResultatsContract.View,
    private val repository: RecetteRepository
) : ResultatsContract.Presenter {

    override fun chargerResultats(
        filtres:           FiltreRecette,
        ingredientsDispos: List<IngredientQuantite>
    ) {
        val recettes = repository.findParFiltres(filtres)

        val recettesAvecScore = recettes.map { recette ->
            val ingredientsRecette = repository.findIngredientQuantitesParRecette(recette.id)
            val score = ScoreCalculateur.calculer(ingredientsRecette, ingredientsDispos)
            RecetteAvecScore(recette, score)
        }

        val resultats = recettesAvecScore.sortedByDescending { it.score }

        if (resultats.isEmpty()) {
            vue.afficherEtatVide()
        } else {
            vue.afficherNombreResultats(resultats.size)
            vue.afficherResultats(resultats)
        }
    }
}