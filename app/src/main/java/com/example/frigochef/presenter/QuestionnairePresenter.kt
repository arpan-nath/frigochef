package com.example.frigochef.presenter

import com.example.frigochef.contract.QuestionnaireContract
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.model.repository.SessionRepository
import com.example.frigochef.model.entity.FiltreRecette

class QuestionnairePresenter(
    private val vue: QuestionnaireContract.View,
    private val ingredientRepository: IngredientRepository,
    private val sessionRepository: SessionRepository
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

    // sauvegarde chaque ingredient coché dans la session et retourne vers les resultats
    override fun valider(filtres: FiltreRecette, ingredientsCoches: List<Long>){
        ingredientsCoches.forEach{id ->
            sessionRepository.upsert(id)
        }
        vue.naviguerVersResultats(filtres, ingredientsCoches)
    }
}