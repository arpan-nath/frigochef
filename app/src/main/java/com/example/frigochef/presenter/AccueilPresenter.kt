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
            // Si la barre de recherche est vide, recharge toutes les recettes
            val recettes = if (query.isEmpty()) repository.findAll()
            else repository.findParNom(query)
            if (recettes.isEmpty()) vue.afficherMessageVide()
            else vue.afficherRecettes(recettes)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors de la recherche.")
        }
    }

    /**
     * Filtre les recettes selon un FiltreRecette combiné qui peut contenir
     * plusieurs critères simultanément (difficulté, végé, temps max, etc.).
     * Remplace les anciennes méthodes filtrerParDifficulte(), filtrerParDiete()
     * et filtrerParTemps() qui ne permettaient pas la combinaison de filtres.
     */
    override fun filtrerParFiltres(filtres: FiltreRecette) {
        try {
            val recettes = repository.findParFiltres(filtres)
            if (recettes.isEmpty()) vue.afficherMessageVide()
            else vue.afficherRecettes(recettes)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors du filtrage.")
        }
    }

    /**
     * Récupère les IDs des ingrédients de la dernière session depuis SessionRepository,
     * retrouve leurs noms via IngredientRepository, et envoie la liste à la Vue.
     * La logique d'accès aux données reste dans le Présentateur (respect du MVP) —
     * la Vue reçoit uniquement une liste de noms prête à afficher.
     */
    override fun chargerSessionIngredients() {
        try {
            val ids  = sessionRepository.findAllIds()
            // Prend au maximum 3 ingrédients pour l'affichage dans le hero
            val noms = ids.take(3).mapNotNull { ingredientRepository.findById(it)?.nom }
            vue.afficherChipsSession(noms)
        } catch (e: Exception) {
            vue.afficherErreur("Erreur lors du chargement de la session.")
        }
    }
}