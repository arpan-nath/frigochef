package com.example.frigochef.presenter

import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.RecetteAvecScore
import com.example.frigochef.model.repository.RecetteRepository
import com.example.frigochef.model.ScoreCalculateur
import com.example.frigochef.contract.ResultatsContract

class ResultatsPresenter(
    private val vue:        ResultatsContract.View,
    private val repository: RecetteRepository
) : ResultatsContract.Presenter {

    override fun chargerResultats(
        filtres:           FiltreRecette,
        ingredientsDispos: List<Long>
    ) {
        val recettes = repository.findParFiltres(filtres)

        val recettesAvecScore = recettes.map { recette ->
            val ingredientsRecette = repository.findIngredientIdsParRecette(recette.id)
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