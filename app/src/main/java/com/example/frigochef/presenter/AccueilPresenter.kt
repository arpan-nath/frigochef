package com.example.frigochef.presenter

import com.example.frigochef.contract.AccueilContract
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.model.repository.RecetteRepository
import com.example.frigochef.model.repository.SessionRepository

class AccueilPresenter(
    private val vue:                  AccueilContract.View,
    private val repository:           RecetteRepository,
    private val sessionRepository:    SessionRepository,
    private val ingredientRepository: IngredientRepository
) : AccueilContract.Presenter {

    override fun chargerRecettes() {
        try {
            val recettes = repository.findAll()
            if (recettes.isEmpty()) vue.afficherMessageVide()
            else vue.afficherRecettes(recettes)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors du chargement des recettes.")
        }
    }

    override fun rechercherRecettes(query: String) {
        try {
            val recettes = if (query.isEmpty()) repository.findAll()
            else repository.findParNom(query)
            if (recettes.isEmpty()) vue.afficherMessageVide()
            else vue.afficherRecettes(recettes)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors de la recherche.")
        }
    }

    override fun filtrerParFiltres(filtres: FiltreRecette) {
        try {
            val recettes = repository.findParFiltres(filtres)
            if (recettes.isEmpty()) vue.afficherMessageVide()
            else vue.afficherRecettes(recettes)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors du filtrage.")
        }
    }

    override fun chargerSessionIngredients() {
        try {
            val ids  = sessionRepository.findAllIds()
            val noms = ids.take(3).mapNotNull { ingredientRepository.findById(it)?.nom }
            vue.afficherChipsSession(noms)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors du chargement de la session.")
        }
    }
}