package com.example.frigochef.presenter

import com.example.frigochef.contract.QuestionnaireContract
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.model.repository.SessionRepository
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.repository.RecetteRepository
/**
 * Gère la logique métier du questionnaire sans aucune dépendance Android.
 * Charge les IDs de la session précédente depuis SessionRepository et les transmet à la vue.
 * Recherche les ingrédients par nom via IngredientRepository (requête SQL LIKE).
 * À la validation, sauvegarde les ingrédients cochés en session via upsert(), puis calcule
 * les recettes admissibles : une requête findParFiltres() par cuisine sélectionnée,
 * fusion avec flatMap et déduplication avec distinctBy { it.id }.
 * Transmet les IDs pré-filtrés à la vue pour éviter de modifier FiltreRecette.
 */
class QuestionnairePresenter(
    private val vue: QuestionnaireContract.View,
    private val ingredientRepository: IngredientRepository,
    private val sessionRepository: SessionRepository,
    private val recetteRepository: RecetteRepository
): QuestionnaireContract.Presenter{

    // Charger les ingredients de la derniere session et les envoie a la vue pour les pré-cocher dans l'étape 3
    override fun chargerSessionPrecedente() {
        val ids = sessionRepository.findAllIds()
        if(ids.isNotEmpty()){
            vue.afficherIngredientsPrecaches(ids)
        }
    }

    // Rechercher les ingredients que le user saisie
    override fun rechercherIngredient(query: String){
        if(query.isBlank()) return
        val resultats = ingredientRepository.findParNom(query)
        vue.afficherIngredientsSuggeres(resultats)
    }

    // reçoit toute la liste de cuisines sélectionnées:
    // si plusieurs cuisines, on fait une requête par cuisine et on fusionne les résultats
    override fun valider(
        filtres:               FiltreRecette,
        ingredientsCoches:     List<Long>,
        cuisinesSelectionnees: List<String>
    ) {
        // Sauvegarder chaque ingrédient coché dans la session
        ingredientsCoches.forEach { id -> sessionRepository.upsert(id) }

        // Calculer les IDs de recettes admissibles selon les cuisines choisies
        val recettesPrefiltrées: List<Long> = when {

            // Aucune cuisine choisie → liste vide = pas de filtre cuisine, toutes les cuisines passent
            cuisinesSelectionnees.isEmpty() -> emptyList()

            // Une seule cuisine → une requête simple avec typeCuisine
            cuisinesSelectionnees.size == 1 -> {
                val filtreCuisine = filtres.copy(typeCuisine = cuisinesSelectionnees.first())
                recetteRepository.findParFiltres(filtreCuisine).map { it.id }
            }

            // Plusieurs cuisines → une requête par cuisine, fusion et déduplication par ID
            else -> {
                // On retire typeCuisine du filtre de base pour ne pas le doubler
                val filtresSansCuisine = filtres.copy(typeCuisine = null)
                cuisinesSelectionnees
                    .flatMap { cuisine ->
                        val filtreCuisine = filtresSansCuisine.copy(typeCuisine = cuisine)
                        recetteRepository.findParFiltres(filtreCuisine)
                    }
                    .distinctBy { it.id }   // déduplication si une recette apparaît plusieurs fois
                    .map { it.id }
            }
        }

        // On passe filtres SANS typeCuisine : le filtre cuisine est déjà appliqué via recettesPrefiltrées
        val filtresSansCuisine = filtres.copy(typeCuisine = null)
        vue.naviguerVersResultats(filtresSansCuisine, ingredientsCoches, recettesPrefiltrées)
    }


}