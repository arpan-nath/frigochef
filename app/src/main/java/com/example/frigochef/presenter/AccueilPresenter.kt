package com.example.frigochef.presenter

import com.example.frigochef.contract.AccueilContract
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.repository.RecetteRepository

class AccueilPresentateur(
    private val vue: AccueilContract.View,
    private val repository: RecetteRepository
) : AccueilContract.Presenter {

    override fun chargerRecettes() {
        try {
            val recettes = repository.findAll()  // ← findAll()
            if (recettes.isEmpty()) {
                vue.afficherMessageVide()
            } else {
                vue.afficherRecettes(recettes)
            }
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors du chargement des recettes.")
        }
    }

    override fun rechercherRecettes(query: String) {
        try {
            val recettes = if (query.isEmpty()) {
                repository.findAll()             // ← si vide, tout charger
            } else {
                repository.findParNom(query)     // ← findParNom()
            }
            if (recettes.isEmpty()) {
                vue.afficherMessageVide()
            } else {
                vue.afficherRecettes(recettes)
            }
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors de la recherche.")
        }
    }

    override fun filtrerParDifficulte(difficulte: String) {
        try {
            val filtres = FiltreRecette(difficulte = difficulte)
            val recettes = repository.findParFiltres(filtres)
            if (recettes.isEmpty()) vue.afficherMessageVide()
            else vue.afficherRecettes(recettes)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors du filtrage.")
        }
    }

    override fun filtrerParDiete(isVege: Boolean) {
        try {
            val filtres = FiltreRecette(isVege = isVege)
            val recettes = repository.findParFiltres(filtres)
            if (recettes.isEmpty()) vue.afficherMessageVide()
            else vue.afficherRecettes(recettes)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors du filtrage.")
        }
    }

    override fun filtrerParTemps(tempsMax: Int) {
        try {
            val filtres = FiltreRecette(tempsMax = tempsMax)
            val recettes = repository.findParFiltres(filtres)
            if (recettes.isEmpty()) vue.afficherMessageVide()
            else vue.afficherRecettes(recettes)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors du filtrage.")
        }
    }
}