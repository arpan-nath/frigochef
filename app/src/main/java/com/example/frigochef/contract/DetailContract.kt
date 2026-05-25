package com.example.frigochef.contract

import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.entity.RecetteIngredientDetail

interface DetailContract {

    interface View {
        fun afficherRecette(recette: Recette)
        fun afficherIngredients(
            ingredients:       List<RecetteIngredientDetail>,
            ingredientsDispos: List<IngredientQuantite>
        )
        fun afficherErreur(message: String)
    }

    interface Presenter {
        fun chargerDetail(recetteId: Long, ingredientsDispos: List<IngredientQuantite>)
    }
}