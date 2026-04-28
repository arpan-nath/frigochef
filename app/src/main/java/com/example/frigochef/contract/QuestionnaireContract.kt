package com.example.frigochef.view.contract

import com.example.frigochef.model.entity.Ingredient

interface QuestionnaireContract {

    interface View {
        fun afficherIngredientsSuggeres(ingredients: List<Ingredient>)
        fun afficherIngredientsPrecaches(ids: List<Long>)
        fun naviguerVersResultats(
            typeCuisine:  String?,
            typeRepas:    String?,
            difficulte:   String?,
            tempsMax:     Int?,
            isVege:       Boolean,
            isVegan:      Boolean,
            isSansGluten: Boolean,
            ingredientsCoches: List<Long>
        )
    }

    interface Presenter {
        fun chargerSessionPrecedente()
        fun rechercherIngredient(query: String)
        fun valider(
            typeCuisine:  String?,
            typeRepas:    String?,
            difficulte:   String?,
            tempsMax:     Int?,
            isVege:       Boolean,
            isVegan:      Boolean,
            isSansGluten: Boolean,
            ingredientsCoches: List<Long>
        )
    }
}