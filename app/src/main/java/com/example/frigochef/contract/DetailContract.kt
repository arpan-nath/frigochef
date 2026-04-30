package com.example.frigochef.view.contract

import com.example.frigochef.model.entity.Ingredient
import com.example.frigochef.model.entity.Recette

interface DetailContract {

    interface View {
        fun afficherRecette(recette: Recette)
        fun afficherIngredients(
            ingredients:       List<Ingredient>,
            ingredientsDispos: List<Long>
        )
        fun afficherErreur(message: String)
    }

    interface Presenter {
        fun chargerDetail(recetteId: Long, ingredientsDispos: List<Long>)
    }
}