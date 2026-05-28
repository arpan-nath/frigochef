package com.example.frigochef.contract

import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.Ingredient

interface QuestionnaireContract {

    interface View {
        fun afficherIngredientsSuggeres(ingredients: List<Ingredient>)
        fun afficherIngredientsPrecaches(ids: List<Long>)
        fun naviguerVersResultats(
            filtres:             FiltreRecette,
            ingredientsCoches:   List<Long>,
            recettesPrefiltrées: List<Long>
        )
    }

    interface Presenter {
        fun chargerSessionPrecedente()
        fun rechercherIngredient(query: String)

        // Ajout de cuisinesSelectionnees pour gérer la multi-sélection
        fun valider(
            filtres:               FiltreRecette,
            ingredientsCoches:     List<Long>,
            cuisinesSelectionnees: List<String>
        )
    }
}